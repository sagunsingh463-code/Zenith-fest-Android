# Postman Testing Guide: Zenith '26 College Fest Management System

This document provides the full 18-request testing suite for validating the **Zenith '26 College Fest Management System API** (PM2-01 & PM2-02).

---

## 1. Quick Setup in Postman

1. Open the Postman Desktop App.
2. Click **Import** (top left).
3. Drag & drop or browse to select `zenith26-api.postman_collection.json` located in the `zenith26-fest-system/` root directory.
4. Set the Collection Variable:
   - Variable Name: `baseUrl`
   - Initial / Current Value: `http://127.0.0.1:8000`
5. Make sure the FastAPI backend is running:
   ```bash
   .\.venv\Scripts\python.exe -m uvicorn backend.main:app --reload --port 8000
   ```

---

## 2. Test Execution Matrix (18 Tests)

### Group A: Events Discovery (`/api/events`)

#### Test 1: Fetch All Events
- **Method & URL:** `GET {{baseUrl}}/api/events`
- **Expected Status:** `200 OK`
- **Verification:**
  - Returns an array of 14 event objects.
  - Every event includes `available_seats` equal to `capacity` (when zero registrations exist).

#### Test 2: Filter by Category (Technical)
- **Method & URL:** `GET {{baseUrl}}/api/events?category=Technical`
- **Expected Status:** `200 OK`
- **Verification:** All returned events have `"category": "Technical"`.

#### Test 3: Filter by Category (Cultural)
- **Method & URL:** `GET {{baseUrl}}/api/events?category=Cultural`
- **Expected Status:** `200 OK`
- **Verification:** All returned events have `"category": "Cultural"`.

#### Test 4: Search by Keyword
- **Method & URL:** `GET {{baseUrl}}/api/events?search=Code`
- **Expected Status:** `200 OK`
- **Verification:** Returns "Code Sprint 2.0".

#### Test 5: Search Non-Existent Event
- **Method & URL:** `GET {{baseUrl}}/api/events?search=NonExistentKeywordXYZ`
- **Expected Status:** `200 OK`
- **Verification:** Returns an empty array `[]`.

---

### Group B: Single Event Detail (`/api/events/{id}`)

#### Test 6: Get Valid Event Detail
- **Method & URL:** `GET {{baseUrl}}/api/events/1`
- **Expected Status:** `200 OK`
- **Verification:** Returns JSON for "Code Sprint 2.0" with `available_seats: 100`.

#### Test 7: Get Non-Existent Event (404)
- **Method & URL:** `GET {{baseUrl}}/api/events/999`
- **Expected Status:** `404 Not Found`
- **Verification Body:** `{"detail": "Event with id 999 not found"}`.

---

### Group C: Student Registration (`/api/events/{id}/register`)

#### Test 8: Valid Registration (201 Created)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "student_name": "Aarav Sharma",
    "roll_number": "2026CS101",
    "email": "aarav.sharma@college.edu",
    "phone": "+91 9876543210"
  }
  ```
- **Expected Status:** `201 Created`
- **Verification:**
  - Response contains `id`, `event_id: 1`, `student_name: "Aarav Sharma"`, and server timestamp `registered_at`.

#### Test 9: Verify Seat Count Decrement
- **Method & URL:** `GET {{baseUrl}}/api/events/1`
- **Expected Status:** `200 OK`
- **Verification:** `available_seats` is now `99` (reduced from 100).

#### Test 10: Validation - Missing Student Name (400)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "student_name": "",
    "roll_number": "2026CS102",
    "email": "student2@college.edu"
  }
  ```
- **Expected Status:** `400 Bad Request`
- **Verification Body:** `{"detail": "Student name, roll number, and email are required and cannot be empty"}`.

#### Test 11: Validation - Invalid Email Format (400)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "student_name": "Rohan Verma",
    "roll_number": "2026CS103",
    "email": "rohan_invalid_email"
  }
  ```
- **Expected Status:** `400 Bad Request`
- **Verification Body:** Contains `"must contain '@'"`.

#### Test 12: Business Logic - Duplicate Email Prevention (409)
- **Method & URL:** `POST {{baseUrl}}/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "student_name": "Aarav Sharma",
    "roll_number": "2026CS101",
    "email": "aarav.sharma@college.edu"
  }
  ```
- **Expected Status:** `409 Conflict`
- **Verification Body:** Contains `"Already registered"`.

#### Test 13: Register on Non-Existent Event (404)
- **Method & URL:** `POST {{baseUrl}}/api/events/999/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "student_name": "Tara Singh",
    "roll_number": "2026EE105",
    "email": "tara@college.edu"
  }
  ```
- **Expected Status:** `404 Not Found`

---

### Group D: Registrations List (`/api/registrations`)

#### Test 14: List All Registrations
- **Method & URL:** `GET {{baseUrl}}/api/registrations`
- **Expected Status:** `200 OK`
- **Verification:** Array includes Aarav Sharma's registration with `event_title: "Code Sprint 2.0"`.

#### Test 15: Filter Registrations by Event
- **Method & URL:** `GET {{baseUrl}}/api/registrations?event_id=1`
- **Expected Status:** `200 OK`
- **Verification:** Only returns registrations for event ID 1.

---

### Group E: Live Stats (`/api/stats`)

#### Test 16: Retrieve Computed Fest Stats
- **Method & URL:** `GET {{baseUrl}}/api/stats`
- **Expected Status:** `200 OK`
- **Verification:**
  - `total_events`: 14
  - `total_registrations`: at least 1
  - `overall_utilization_pct`: calculated as `(total_registrations / total_capacity) * 100` rounded to 1 decimal.
  - `events_by_category`: counts mapped by category.

---

### Group F: Stretch Management Capabilities

#### Test 17: Create New Event (POST `/api/events`)
- **Method & URL:** `POST {{baseUrl}}/api/events`
- **Headers:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "title": "Robo Soccer Cup",
    "category": "Technical",
    "icon": "⚽",
    "event_date": "2026-11-15",
    "event_time": "03:00 PM",
    "venue": "Robotics Arena",
    "capacity": 32,
    "fee": 50,
    "description": "Autonomous bot soccer championship with knockouts."
  }
  ```
- **Expected Status:** `201 Created`
- **Verification:** Returns created event with assigned `id: 15`.

#### Test 18: Cancel Registration (DELETE `/api/registrations/{id}`)
- **Method & URL:** `DELETE {{baseUrl}}/api/registrations/1`
- **Expected Status:** `200 OK`
- **Verification:** Returns `{"message": "Registration cancelled successfully", "id": 1}`.
- Re-checking `GET /api/events/1` shows `available_seats` restored back to `100`.
