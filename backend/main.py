from contextlib import asynccontextmanager
from datetime import date, datetime
from pathlib import Path
from typing import Dict, List, Optional
import sqlite3

from fastapi import FastAPI, HTTPException, Query, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

from backend.database import get_db_connection, init_db
from backend.models import (
    EventCreateIn,
    EventDetail,
    EventOut,
    RegistrationIn,
    RegistrationItemOut,
    RegistrationOut,
    StatsOut,
)
from backend.seed_data import seed_events

BASE_DIR = Path(__file__).resolve().parent.parent
FRONTEND_DIR = BASE_DIR / "frontend"


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Lifecycle context manager: initializes database and runs seeding on startup."""
    init_db()
    try:
        seed_events()
    except Exception as e:
        print(f"Warning during seed execution: {e}")
    yield


app = FastAPI(
    title="Zenith '26 College Fest Management System API",
    description="Backend API for Zenith '26 fest student portal and admin dashboard",
    version="1.0.0",
    lifespan=lifespan,
)

# Enable CORS for local development
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==============================================================================
# Helper Functions
# ==============================================================================

def get_event_available_seats(cursor: sqlite3.Cursor, event_id: int, capacity: int) -> int:
    """Computes available seats as capacity minus live registrations count."""
    cursor.execute("SELECT COUNT(*) FROM registrations WHERE event_id = ?", (event_id,))
    reg_count = cursor.fetchone()[0]
    return max(0, capacity - reg_count)


# ==============================================================================
# API Endpoints
# ==============================================================================

@app.get(
    "/api/events",
    response_model=List[EventOut],
    summary="List all fest events with optional category and search filters",
)
@app.get(
    "/events",
    response_model=List[EventOut],
    summary="List all fest events with optional category and search filters",
)
def list_events(
    category: Optional[str] = Query(default=None, description="Filter by event category"),
    search: Optional[str] = Query(default=None, description="Case-insensitive title search"),
):
    conn = get_db_connection()
    cursor = conn.cursor()

    query = "SELECT * FROM events WHERE 1=1"
    params = []

    if category and category.strip().lower() != "all":
        query += " AND LOWER(category) = LOWER(?)"
        params.append(category.strip())

    if search and search.strip():
        query += " AND LOWER(title) LIKE LOWER(?)"
        params.append(f"%{search.strip()}%")

    query += " ORDER BY id ASC"
    cursor.execute(query, params)
    rows = cursor.fetchall()

    results: List[EventOut] = []
    for row in rows:
        avail = get_event_available_seats(cursor, row["id"], row["capacity"])
        results.append(
            EventOut(
                id=row["id"],
                title=row["title"],
                category=row["category"],
                description=row["description"],
                event_date=row["event_date"],
                event_time=row["event_time"],
                venue=row["venue"],
                capacity=row["capacity"],
                fee=row["fee"],
                icon=row["icon"],
                available_seats=avail,
            )
        )

    conn.close()
    return results


@app.get(
    "/api/events/{event_id}",
    response_model=EventDetail,
    summary="Get details for a specific event",
)
@app.get(
    "/events/{event_id}",
    response_model=EventDetail,
    summary="Get details for a specific event",
)
def get_event(event_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM events WHERE id = ?", (event_id,))
    row = cursor.fetchone()
    if not row:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} not found",
        )

    avail = get_event_available_seats(cursor, row["id"], row["capacity"])
    event_detail = EventDetail(
        id=row["id"],
        title=row["title"],
        category=row["category"],
        description=row["description"],
        event_date=row["event_date"],
        event_time=row["event_time"],
        venue=row["venue"],
        capacity=row["capacity"],
        fee=row["fee"],
        icon=row["icon"],
        available_seats=avail,
    )

    conn.close()
    return event_detail


@app.post(
    "/api/events/{event_id}/register",
    response_model=RegistrationOut,
    status_code=status.HTTP_201_CREATED,
    summary="Register a student for an event with strict server-side validation",
)
@app.post(
    "/events/{event_id}/register",
    response_model=RegistrationOut,
    status_code=status.HTTP_201_CREATED,
    summary="Register a student for an event with strict server-side validation",
)
def register_student(event_id: int, payload: RegistrationIn):
    # 1. Server-side validation of text fields
    name = payload.student_name.strip() if payload.student_name else ""
    roll = payload.roll_number.strip() if payload.roll_number else ""
    email = payload.email.strip().lower() if payload.email else ""
    phone = payload.phone.strip() if payload.phone else None

    if not name or not roll or not email:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Student name, roll number, and email are required and cannot be empty",
        )

    if "@" not in email or email.startswith("@") or email.endswith("@"):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid email address format (must contain '@' with domain)",
        )

    conn = get_db_connection()
    cursor = conn.cursor()

    # 2. Verify event existence
    cursor.execute("SELECT id, title, capacity FROM events WHERE id = ?", (event_id,))
    event_row = cursor.fetchone()
    if not event_row:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} not found",
        )

    # 3. Check duplicate registration (UNIQUE constraint)
    cursor.execute(
        "SELECT id FROM registrations WHERE event_id = ? AND LOWER(email) = ?",
        (event_id, email),
    )
    if cursor.fetchone():
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=f"Already registered: Student with email '{email}' is already registered for this event",
        )

    # 4. Check available capacity
    cursor.execute("SELECT COUNT(*) FROM registrations WHERE event_id = ?", (event_id,))
    reg_count = cursor.fetchone()[0]
    if reg_count >= event_row["capacity"]:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=f"Event is full: Capacity of {event_row['capacity']} has been reached",
        )

    # 5. Insert registration with server timestamp
    registered_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    cursor.execute(
        """
        INSERT INTO registrations (event_id, student_name, roll_number, email, phone, registered_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        (event_id, name, roll, email, phone, registered_at),
    )
    new_reg_id = cursor.lastrowid
    conn.commit()
    conn.close()

    return RegistrationOut(
        id=new_reg_id,
        event_id=event_id,
        student_name=name,
        registered_at=registered_at,
    )


@app.get(
    "/api/registrations",
    response_model=List[RegistrationItemOut],
    summary="List registrations with event details, optionally filtered by event_id",
)
@app.get(
    "/registrations",
    response_model=List[RegistrationItemOut],
    summary="List registrations with event details, optionally filtered by event_id",
)
def list_registrations(
    event_id: Optional[int] = Query(default=None, description="Filter registrations by event ID"),
):
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT r.id, r.event_id, e.title AS event_title, r.student_name,
               r.roll_number, r.email, r.phone, r.registered_at
        FROM registrations r
        JOIN events e ON r.event_id = e.id
        WHERE 1=1
    """
    params = []

    if event_id is not None:
        query += " AND r.event_id = ?"
        params.append(event_id)

    query += " ORDER BY r.id DESC"
    cursor.execute(query, params)
    rows = cursor.fetchall()

    results = [
        RegistrationItemOut(
            id=r["id"],
            event_id=r["event_id"],
            event_title=r["event_title"],
            student_name=r["student_name"],
            roll_number=r["roll_number"],
            email=r["email"],
            phone=r["phone"],
            registered_at=r["registered_at"],
        )
        for r in rows
    ]

    conn.close()
    return results


@app.get(
    "/api/stats",
    response_model=StatsOut,
    summary="Retrieve live festival statistics computed on-the-fly",
)
@app.get(
    "/stats",
    response_model=StatsOut,
    summary="Retrieve live festival statistics computed on-the-fly",
)
def get_stats():
    conn = get_db_connection()
    cursor = conn.cursor()

    # Total events & total capacity
    cursor.execute("SELECT COUNT(*), COALESCE(SUM(capacity), 0) FROM events")
    event_summary = cursor.fetchone()
    total_events = event_summary[0]
    total_capacity = event_summary[1]

    # Total registrations
    cursor.execute("SELECT COUNT(*) FROM registrations")
    total_registrations = cursor.fetchone()[0]

    # Overall utilization percentage
    overall_utilization_pct = 0.0
    if total_capacity > 0:
        overall_utilization_pct = round((total_registrations / total_capacity) * 100, 1)

    # Upcoming events count (event_date today or later)
    today_str = date.today().isoformat()
    cursor.execute("SELECT COUNT(*) FROM events WHERE event_date >= ?", (today_str,))
    upcoming_events_count = cursor.fetchone()[0]

    # Events breakdown by category
    cursor.execute("SELECT category, COUNT(*) as count FROM events GROUP BY category")
    cat_rows = cursor.fetchall()
    events_by_category: Dict[str, int] = {r["category"]: r["count"] for r in cat_rows}

    conn.close()

    return StatsOut(
        total_events=total_events,
        total_registrations=total_registrations,
        total_capacity=total_capacity,
        overall_utilization_pct=overall_utilization_pct,
        upcoming_events_count=upcoming_events_count,
        events_by_category=events_by_category,
    )


# ==============================================================================
# Stretch Goals Endpoints
# ==============================================================================

@app.post(
    "/api/events",
    response_model=EventOut,
    status_code=status.HTTP_201_CREATED,
    summary="[Stretch Goal] Create a new event with validation",
)
@app.post(
    "/events",
    response_model=EventOut,
    status_code=status.HTTP_201_CREATED,
    summary="[Stretch Goal] Create a new event with validation",
)
def create_event(payload: EventCreateIn):
    title = payload.title.strip()
    category = payload.category.strip()
    venue = payload.venue.strip()
    event_date = payload.event_date.strip()
    event_time = payload.event_time.strip()

    if not title or not category or not venue or not event_date or not event_time:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Title, category, venue, event_date, and event_time are required",
        )

    if payload.capacity <= 0:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Capacity must be greater than 0",
        )

    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute(
        """
        INSERT INTO events (title, category, description, event_date, event_time, venue, capacity, fee, icon)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        (
            title,
            category,
            payload.description.strip() if payload.description else "",
            event_date,
            event_time,
            venue,
            payload.capacity,
            payload.fee,
            payload.icon.strip() if payload.icon else "🎯",
        ),
    )
    new_id = cursor.lastrowid
    conn.commit()

    avail = payload.capacity
    created = EventOut(
        id=new_id,
        title=title,
        category=category,
        description=payload.description,
        event_date=event_date,
        event_time=event_time,
        venue=venue,
        capacity=payload.capacity,
        fee=payload.fee,
        icon=payload.icon,
        available_seats=avail,
    )

    conn.close()
    return created


@app.delete(
    "/api/registrations/{registration_id}",
    summary="[Stretch Goal] Cancel a registration",
)
@app.delete(
    "/registrations/{registration_id}",
    summary="[Stretch Goal] Cancel a registration",
)
def cancel_registration(registration_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT id, event_id, student_name FROM registrations WHERE id = ?", (registration_id,))
    reg = cursor.fetchone()
    if not reg:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Registration with id {registration_id} not found",
        )

    cursor.execute("DELETE FROM registrations WHERE id = ?", (registration_id,))
    conn.commit()
    conn.close()

    return {
        "message": "Registration cancelled successfully",
        "id": registration_id,
        "event_id": reg["event_id"],
        "student_name": reg["student_name"],
    }


# ==============================================================================
# Static File & Frontend Mounting (Step 4)
# ==============================================================================

# Mount javascript files directory
if (FRONTEND_DIR / "js").exists():
    app.mount("/js", StaticFiles(directory=str(FRONTEND_DIR / "js")), name="js")

@app.get("/", response_class=FileResponse, summary="Serve student event portal")
def serve_index():
    index_path = FRONTEND_DIR / "index.html"
    if not index_path.exists():
        raise HTTPException(status_code=404, detail="index.html not found")
    return FileResponse(index_path)

@app.get("/admin.html", response_class=FileResponse, summary="Serve admin dashboard")
def serve_admin():
    admin_path = FRONTEND_DIR / "admin.html"
    if not admin_path.exists():
        raise HTTPException(status_code=404, detail="admin.html not found")
    return FileResponse(admin_path)

@app.get("/mobile", response_class=FileResponse, summary="Serve interactive mobile app")
@app.get("/mobile.html", response_class=FileResponse, summary="Serve interactive mobile app")
def serve_mobile():
    mobile_path = FRONTEND_DIR / "mobile.html"
    if not mobile_path.exists():
        raise HTTPException(status_code=404, detail="mobile.html not found")
    return FileResponse(mobile_path)

@app.get("/Zenith26.apk", response_class=FileResponse, summary="Download Android APK")
@app.get("/app-debug.apk", response_class=FileResponse, summary="Download Android APK")
def download_apk():
    apk_path = FRONTEND_DIR / "Zenith26.apk"
    if not apk_path.exists():
        raise HTTPException(status_code=404, detail="Zenith26.apk not found")
    return FileResponse(
        apk_path,
        media_type="application/vnd.android.package-archive",
        filename="Zenith26.apk"
    )

@app.get("/sitebundle.zip", response_class=FileResponse, summary="Download WebView site bundle")
def download_sitebundle():
    bundle_path = FRONTEND_DIR / "sitebundle.zip"
    if not bundle_path.exists():
        raise HTTPException(status_code=404, detail="sitebundle.zip not found")
    return FileResponse(
        bundle_path,
        media_type="application/zip",
        filename="sitebundle.zip"
    )

# Mount frontend directory for additional static assets (like favicon or styles)
if FRONTEND_DIR.exists():
    app.mount("/static", StaticFiles(directory=str(FRONTEND_DIR)), name="static")
