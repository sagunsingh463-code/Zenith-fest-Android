import os
import shutil
import sqlite3
from pathlib import Path
from typing import Any, List, Optional, Tuple, Union

try:
    import psycopg2
    import psycopg2.extras
    HAS_PSYCOPG2 = True
except ImportError:
    HAS_PSYCOPG2 = False

BASE_DIR = Path(__file__).resolve().parent.parent
DB_PATH = BASE_DIR / "fest.db"


def get_database_url() -> Optional[str]:
    """Detects PostgreSQL connection URL from standard environment variables (Vercel, Supabase, Neon)."""
    for key in ["DATABASE_URL", "POSTGRES_URL", "POSTGRES_PRISMA_URL", "POSTGRES_URL_NON_POOLING"]:
        url = os.environ.get(key)
        if url and (url.startswith("postgres://") or url.startswith("postgresql://")):
            if url.startswith("postgres://"):
                url = url.replace("postgres://", "postgresql://", 1)
            return url
    return None


def is_postgres() -> bool:
    """Returns True if PostgreSQL is configured and psycopg2 is available."""
    return bool(get_database_url() and HAS_PSYCOPG2)


class PostgresCursorWrapper:
    """Cursor wrapper that transparently translates SQLite syntax (?) to PostgreSQL (%s)."""

    def __init__(self, cursor):
        self._cursor = cursor
        self.lastrowid: Optional[int] = None

    def execute(self, query: str, params: Union[Tuple, List, None] = None):
        # Convert ? placeholders to %s for psycopg2
        pg_query = query.replace("?", "%s")

        # For INSERT queries without explicit RETURNING, append RETURNING id to populate lastrowid
        is_insert = pg_query.strip().upper().startswith("INSERT INTO")
        has_returning = "RETURNING" in pg_query.upper()

        if is_insert and not has_returning:
            clean_query = pg_query.rstrip(" \t\r\n;")
            pg_query = f"{clean_query} RETURNING id;"

        if params is not None:
            self._cursor.execute(pg_query, params)
        else:
            self._cursor.execute(pg_query)

        if is_insert:
            try:
                row = self._cursor.fetchone()
                if row:
                    self.lastrowid = row[0]
            except Exception:
                pass

        return self

    def fetchone(self):
        return self._cursor.fetchone()

    def fetchall(self):
        return self._cursor.fetchall()

    def close(self):
        self._cursor.close()


class PostgresConnectionWrapper:
    """Connection wrapper ensuring row index and key access match sqlite3.Row."""

    def __init__(self, conn):
        self._conn = conn

    def cursor(self):
        return PostgresCursorWrapper(self._conn.cursor(cursor_factory=psycopg2.extras.DictCursor))

    def commit(self):
        self._conn.commit()

    def rollback(self):
        self._conn.rollback()

    def close(self):
        self._conn.close()


def get_db_connection():
    """Returns a connection to PostgreSQL if configured, otherwise falls back to SQLite."""
    pg_url = get_database_url()
    if pg_url and HAS_PSYCOPG2:
        conn = psycopg2.connect(pg_url)
        return PostgresConnectionWrapper(conn)

    # SQLite Fallback (Local development & fallback)
    if os.environ.get("VERCEL"):
        tmp_db = Path("/tmp/fest.db")
        if not tmp_db.exists() and DB_PATH.exists():
            try:
                shutil.copy2(DB_PATH, tmp_db)
            except Exception:
                pass
        conn = sqlite3.connect(tmp_db)
    else:
        conn = sqlite3.connect(DB_PATH)

    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA foreign_keys = ON;")
    return conn


def init_db() -> None:
    """Initializes tables for either PostgreSQL or SQLite."""
    conn = get_db_connection()
    cursor = conn.cursor()

    if is_postgres():
        # PostgreSQL Schema
        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS events (
                id SERIAL PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                category VARCHAR(100) NOT NULL,
                description TEXT,
                event_date VARCHAR(50) NOT NULL,
                event_time VARCHAR(50) NOT NULL,
                venue VARCHAR(255) NOT NULL,
                capacity INTEGER NOT NULL,
                fee INTEGER NOT NULL DEFAULT 0,
                icon VARCHAR(50)
            );
            """
        )

        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS registrations (
                id SERIAL PRIMARY KEY,
                event_id INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                student_name VARCHAR(255) NOT NULL,
                roll_number VARCHAR(100) NOT NULL,
                email VARCHAR(255) NOT NULL,
                phone VARCHAR(50),
                registered_at VARCHAR(50) NOT NULL,
                UNIQUE(event_id, email)
            );
            """
        )
    else:
        # SQLite Schema
        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                category TEXT NOT NULL,
                description TEXT,
                event_date TEXT NOT NULL,
                event_time TEXT NOT NULL,
                venue TEXT NOT NULL,
                capacity INTEGER NOT NULL,
                fee INTEGER NOT NULL DEFAULT 0,
                icon TEXT
            );
            """
        )

        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS registrations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                event_id INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                student_name TEXT NOT NULL,
                roll_number TEXT NOT NULL,
                email TEXT NOT NULL,
                phone TEXT,
                registered_at TEXT NOT NULL,
                UNIQUE(event_id, email)
            );
            """
        )

    conn.commit()
    conn.close()


def migrate_sqlite_to_postgres() -> bool:
    """Migrates all records from local fest.db SQLite database into PostgreSQL."""
    pg_url = get_database_url()
    if not pg_url or not HAS_PSYCOPG2:
        print("Cannot migrate: PostgreSQL is not configured or psycopg2 is missing.")
        return False

    if not DB_PATH.exists():
        print(f"Cannot migrate: SQLite source {DB_PATH} not found.")
        return False

    print("Connecting to SQLite source...")
    s_conn = sqlite3.connect(DB_PATH)
    s_conn.row_factory = sqlite3.Row
    s_cur = s_conn.cursor()

    print("Connecting to PostgreSQL target...")
    p_conn = psycopg2.connect(pg_url)
    p_cur = p_conn.cursor()

    init_db()

    # Migrate events
    s_cur.execute("SELECT * FROM events ORDER BY id ASC")
    events = s_cur.fetchall()
    print(f"Migrating {len(events)} events to PostgreSQL...")

    for ev in events:
        p_cur.execute(
            """
            INSERT INTO events (id, title, category, description, event_date, event_time, venue, capacity, fee, icon)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO UPDATE SET
                title = EXCLUDED.title,
                category = EXCLUDED.category,
                description = EXCLUDED.description,
                event_date = EXCLUDED.event_date,
                event_time = EXCLUDED.event_time,
                venue = EXCLUDED.venue,
                capacity = EXCLUDED.capacity,
                fee = EXCLUDED.fee,
                icon = EXCLUDED.icon;
            """,
            (
                ev["id"], ev["title"], ev["category"], ev["description"],
                ev["event_date"], ev["event_time"], ev["venue"],
                ev["capacity"], ev["fee"], ev["icon"]
            )
        )

    # Sync sequence for events id
    p_cur.execute("SELECT setval(pg_get_serial_sequence('events', 'id'), COALESCE(MAX(id), 1)) FROM events;")

    # Migrate registrations
    s_cur.execute("SELECT * FROM registrations ORDER BY id ASC")
    registrations = s_cur.fetchall()
    print(f"Migrating {len(registrations)} registrations to PostgreSQL...")

    for reg in registrations:
        p_cur.execute(
            """
            INSERT INTO registrations (id, event_id, student_name, roll_number, email, phone, registered_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING;
            """,
            (
                reg["id"], reg["event_id"], reg["student_name"],
                reg["roll_number"], reg["email"], reg["phone"], reg["registered_at"]
            )
        )

    p_cur.execute("SELECT setval(pg_get_serial_sequence('registrations', 'id'), COALESCE(MAX(id), 1)) FROM registrations;")

    p_conn.commit()
    s_conn.close()
    p_conn.close()
    print("Migration from SQLite to PostgreSQL completed successfully!")
    return True


if __name__ == "__main__":
    init_db()
    if is_postgres():
        print(f"Database initialized in PostgreSQL mode via {get_database_url()[:20]}...")
    else:
        print(f"Database initialized in SQLite mode at {DB_PATH}")
