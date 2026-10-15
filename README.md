# Zenith '26 College Fest Management System ⚡

A modern, responsive full-stack college fest management web application built for **Zenith '26**. The system provides a student-facing event discovery and registration portal alongside a real-time admin operational dashboard with live capacity utilization analytics.

---

## 🛠️ Technology Stack

- **Android Frontend:** Kotlin Native Android Application (Jetpack Compose, Retrofit 2, OkHttp 3, Coroutines, StateFlow, MVVM)
- **Web Frontend:** HTML5, Tailwind CSS (via CDN), Vanilla Modern JavaScript (ES6+)
- **Backend:** Python 3.12, FastAPI, Uvicorn, Pydantic v2
- **Database:** SQLite 3 (with foreign key enforcement and computed seat availability)
- **Data Ingestion:** openpyxl (seeds initial 14 flagship events from `data/events_seed.xlsx`)
- **API Testing:** Postman (`zenith26-api.postman_collection.json`)

---

## 📂 Project Structure

```text
zenith26-fest-system/
├── android/             # Native Kotlin Android Application
│   ├── app/
│   │   ├── src/main/java/com/zenith26/fest/
│   │   │   ├── MainActivity.kt          # Entry activity & screen navigation
│   │   │   ├── ZenithApp.kt             # Application class initializing ApiClient
│   │   │   ├── data/
│   │   │   │   ├── model/EventModels.kt # Data models matching API schemas
│   │   │   │   ├── api/ZenithApiService.kt # Retrofit interface
│   │   │   │   ├── api/ApiClient.kt     # Retrofit/OkHttp client + dynamic Base URL
│   │   │   │   └── repository/ZenithRepository.kt # Repository layer
│   │   │   ├── ui/
│   │   │   │   ├── theme/               # Dark Tailwind theme colors, typography, theme
│   │   │   │   ├── components/          # Cards, dialogs, nav, stats, filters, tables
│   │   │   │   ├── screens/             # StudentPortalScreen & AdminDashboardScreen
│   │   │   │   └── viewmodel/           # StudentViewModel & AdminViewModel
│   │   │   └── AndroidManifest.xml      # Permissions and network security config
│   │   └── build.gradle.kts
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── backend/
│   ├── main.py          # FastAPI application, REST endpoints, static mounting
│   ├── database.py      # SQLite connection & table schema initialization
│   ├── models.py        # Pydantic schemas (EventOut, RegistrationIn, StatsOut, etc.)
│   └── seed_data.py     # openpyxl Excel seed ingestion script
├── frontend/
│   ├── index.html       # Student event portal (cards, search, filter, register modal)
│   ├── admin.html       # Admin dashboard (metrics cards, utilization table, registrations)
│   └── js/
│       ├── app.js       # Student portal logic & client validation
│       └── admin.js     # Admin dashboard logic, sorting, filtering, cancellation
├── data/
│   └── events_seed.xlsx # 14 seed events in Excel format
├── requirements.txt     # Python dependencies
├── README.md            # Project documentation and run guide
├── Postman-Testing-Commands-Zenith26.md # 18-test Postman guide
└── zenith26-api.postman_collection.json # Ready-to-import Postman collection
```

---

## 🚀 How to Run the Application

### 1. Prerequisites
- Python 3.10+ installed on your system.

### 2. Activate the Virtual Environment
From inside the `zenith26-fest-system` folder:

**Windows (PowerShell):**
```powershell
.\.venv\Scripts\Activate.ps1
```

*(Or use the direct python executable `.\.venv\Scripts\python.exe`)*

### 3. Install Dependencies (if not already installed)
```bash
pip install -r requirements.txt
```

### 4. (Optional) Run Seed Script Directly
Database seeding runs automatically on server startup. You can also run it manually:
```bash
python -m backend.seed_data
```

### 5. Start the FastAPI Server
```bash
uvicorn backend.main:app --reload --port 8000
```

### 6. Access the Application
- **Student Event Portal (Web):** [http://localhost:8000/](http://localhost:8000/)
- **Admin Dashboard (Web):** [http://localhost:8000/admin.html](http://localhost:8000/admin.html)
- **Interactive API Documentation (Swagger UI):** [http://localhost:8000/docs](http://localhost:8000/docs)
- **Alternative API Documentation (ReDoc):** [http://localhost:8000/redoc](http://localhost:8000/redoc)

---

## 📱 How to Run the Kotlin Android Application

1. **Open in Android Studio:**
   - Launch **Android Studio**.
   - Select **Open**, and navigate to `zenith26-fest-system/android`.
   - Android Studio will automatically sync the Gradle project (`app/build.gradle.kts`).

2. **Network Setup for Android Emulator:**
   - The Android app is preconfigured to communicate with the host computer's FastAPI backend via `http://10.0.2.2:8000/` (standard Android Emulator loopback).
   - `network_security_config.xml` enables cleartext HTTP for `10.0.2.2`, `127.0.0.1`, and local subnets.
   - You can also change the Base URL at runtime anytime by tapping the **⚙️ Settings** icon in the app's top bar!

3. **Run on Device or Emulator:**
   - Start an Android Emulator (API 26+) or connect a physical Android device via USB debugging.
   - Click **Run 'app'** (`Shift + F10`) in Android Studio.
   - Discover events, filter by category, search live, register students, and inspect admin metrics!

---

## 📡 API Endpoints Reference

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `GET` | `/api/events` | List events with optional `?category=` and `?search=` filters | `200 OK` |
| `GET` | `/api/events/{event_id}` | Retrieve details for a single event | `200 OK`, `404 Not Found` |
| `POST` | `/api/events/{event_id}/register` | Register a student with strict server-side validation | `201 Created`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `GET` | `/api/registrations` | List all registrations (optional `?event_id=`) with event title | `200 OK` |
| `GET` | `/api/stats` | Live computed fest statistics and category breakdown | `200 OK` |
| `POST` | `/api/events` | *(Stretch Goal)* Admin creates a new festival event | `201 Created`, `400 Bad Request` |
| `DELETE` | `/api/registrations/{id}` | *(Stretch Goal)* Admin cancels a registration and frees seat | `200 OK`, `404 Not Found` |

---

## 🧪 Postman API Testing

1. Open Postman.
2. Click **Import** and select `zenith26-api.postman_collection.json`.
3. Ensure variable `baseUrl` is set to `http://127.0.0.1:8000`.
4. Follow the step-by-step test commands in `Postman-Testing-Commands-Zenith26.md` to run all 18 test cases.
