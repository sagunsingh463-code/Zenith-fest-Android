import json
import urllib.request
import urllib.error

import os
base = os.environ.get('ZENITH_BASE_URL', 'http://127.0.0.1:8081')

def test_req(method, path, body=None, expected_status=200):
    url = base + path
    headers = {'Content-Type': 'application/json', 'Accept': 'application/json'}
    data = json.dumps(body).encode('utf-8') if body else None
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            status = resp.status
            content = resp.read().decode('utf-8')
            assert status == expected_status, f'Expected {expected_status}, got {status}'
            return status, json.loads(content) if 'application/json' in resp.headers.get('Content-Type', '') else content
    except urllib.error.HTTPError as e:
        content = e.read().decode('utf-8')
        assert e.code == expected_status, f'Expected {expected_status}, got {e.code}: {content}'
        try:
            return e.code, json.loads(content)
        except Exception:
            return e.code, content

print('--- Zenith 26 Test Suite Starting ---')

# 1. HTML Pages
s, r = test_req('GET', '/')
assert '<!DOCTYPE html>' in r and 'ZENITH' in r
print('PASS 1: GET / (index.html) -> 200 OK')

s, r = test_req('GET', '/admin.html')
assert '<!DOCTYPE html>' in r and 'Admin Dashboard' in r
print('PASS 2: GET /admin.html -> 200 OK')

# 2. JS files
s, r = test_req('GET', '/js/app.js')
assert 'fetchEvents' in r
print('PASS 3: GET /js/app.js -> 200 OK')

s, r = test_req('GET', '/js/admin.js')
assert 'loadStats' in r
print('PASS 4: GET /js/admin.js -> 200 OK')

# 3. GET /api/events
s, events = test_req('GET', '/api/events')
assert len(events) >= 14
assert all('available_seats' in ev for ev in events)
print(f'PASS 5: GET /api/events -> 200 OK ({len(events)} events loaded)')

# 4. Filters
s, tech = test_req('GET', '/api/events?category=Technical')
assert all(e['category'] == 'Technical' for e in tech)
print(f'PASS 6: GET /api/events?category=Technical -> 200 OK ({len(tech)} technical events)')

s, search = test_req('GET', '/api/events?search=Code')
assert len(search) >= 1 and 'Code' in search[0]['title']
print(f'PASS 7: GET /api/events?search=Code -> 200 OK (Found: {search[0]["title"]})')

# 5. Event Detail
s, ev1 = test_req('GET', '/api/events/1')
assert ev1['id'] == 1 and ev1['capacity'] == 100
initial_seats = ev1['available_seats']
print(f'PASS 8: GET /api/events/1 -> 200 OK (Seats: {initial_seats}/{ev1["capacity"]})')

# 6. Event 404
s, err404 = test_req('GET', '/api/events/999', expected_status=404)
print(f'PASS 9: GET /api/events/999 -> 404 Not Found ({err404["detail"]})')

# 7. Register Student (201)
reg_payload = {
    'student_name': 'Aarav Sharma',
    'roll_number': '2026CS101',
    'email': 'aarav.sharma@college.edu',
    'phone': '+91 9876543210'
}
s, reg_resp = test_req('POST', '/api/events/1/register', body=reg_payload, expected_status=201)
reg_id = reg_resp['id']
print(f'PASS 10: POST /api/events/1/register -> 201 Created (Reg ID: {reg_id})')

# 8. Check seats decremented
s, ev1_after = test_req('GET', '/api/events/1')
assert ev1_after['available_seats'] == initial_seats - 1
print(f'PASS 11: Seat decremented successfully: {ev1_after["available_seats"]} seats left')

# 9. Register Duplicate (409)
s, err409 = test_req('POST', '/api/events/1/register', body=reg_payload, expected_status=409)
print(f'PASS 12: Duplicate Registration -> 409 Conflict ({err409["detail"]})')

# 10. Register Missing Name (400)
bad_payload = {'student_name': '', 'roll_number': '2026CS102', 'email': 'student@college.edu'}
s, err400 = test_req('POST', '/api/events/1/register', body=bad_payload, expected_status=400)
print(f'PASS 13: Missing Field Validation -> 400 Bad Request ({err400["detail"]})')

# 11. Register Invalid Email (400)
bad_email = {'student_name': 'Rohan', 'roll_number': '2026CS103', 'email': 'invalid_email'}
s, err_email = test_req('POST', '/api/events/1/register', body=bad_email, expected_status=400)
print(f'PASS 14: Invalid Email Validation -> 400 Bad Request ({err_email["detail"]})')

# 12. GET /api/registrations
s, regs = test_req('GET', '/api/registrations')
assert len(regs) >= 1
assert any(r['email'] == 'aarav.sharma@college.edu' and 'event_title' in r for r in regs)
print(f'PASS 15: GET /api/registrations -> 200 OK ({len(regs)} registrations found)')

# 13. GET /api/stats
s, stats = test_req('GET', '/api/stats')
assert stats['total_events'] >= 14
assert stats['total_registrations'] >= 1
assert 'Technical' in stats['events_by_category']
print(f'PASS 16: GET /api/stats -> 200 OK (Total Regs: {stats["total_registrations"]}, Util: {stats["overall_utilization_pct"]}%)')

# 14. Stretch: POST /api/events
new_ev = {
    'title': 'Autonomous Drone Derby',
    'category': 'Technical',
    'icon': '🛸',
    'event_date': '2026-11-15',
    'event_time': '04:00 PM',
    'venue': 'Open Lawn Quad',
    'capacity': 30,
    'fee': 100,
    'description': 'Speed racing and precision obstacle navigation for drones.'
}
s, created_ev = test_req('POST', '/api/events', body=new_ev, expected_status=201)
print(f'PASS 17: POST /api/events -> 201 Created (New Event ID: {created_ev["id"]}, Title: {created_ev["title"]})')

# 15. Stretch: DELETE /api/registrations/{id}
s, del_resp = test_req('DELETE', f'/api/registrations/{reg_id}', expected_status=200)
print(f'PASS 18: DELETE /api/registrations/{reg_id} -> 200 OK ({del_resp["message"]})')

# 16. Verify seat restored
s, ev1_restored = test_req('GET', '/api/events/1')
assert ev1_restored['available_seats'] == initial_seats
print(f'PASS 19: Seat successfully restored back to {ev1_restored["available_seats"]}')

print('\n*** ALL 19 API CONTRACT AND WORKFLOW TESTS PASSED PERFECTLY! ***')
