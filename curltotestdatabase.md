Awesome — here’s a ready-to-run cURL pack to exercise the **new DB shape** you just moved to (times on `ParkBranch`, image URLs + `status`, `BranchVoucher` rename, `Game`↔`ParkBranch`, `BranchAmenity` links, `Shift` with days, `StaffAssignment` scan in/out, and the “get all by branch” queries).

If your paths differ slightly, swap the endpoints to match your controllers (I note you have `ParkBranchController`, `GameController`, `ShiftController`, `StaffAssignmentController`, and currently **BranchPromotionController** — if you haven’t renamed yet, use `/api/branch-promotions` instead of `/api/branch-vouchers`).

---

### 0) (Optional) Login to get a token

```bash
# change email/password to your seeded admin (AdminSeeder)
curl -sS -X POST http://localhost:8080/api/users/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@example.com","password":"Admin@123"}'
```

Copy the JWT from the response and set:

```bash
TOKEN="<paste-jwt-here>"
BASE="http://localhost:8080"
```

---

### 1) Create a Park Branch (LocalTime fields + image + status)

```bash
curl -sS -X POST "$BASE/api/park-branches" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "District 1 Park",
    "address": "1 Le Loi, D1, HCMC",
    "location": "10.776,106.700",
    "openTime": "10:00:00",
    "closeTime": "22:00:00",
    "imageUrl": "https://cdn.example.com/branches/d1.jpg",
    "status": true
  }'
```

List / fetch:

```bash
curl -sS "$BASE/api/park-branches"
curl -sS "$BASE/api/park-branches/1"
```

---

### 2) Create a Game (belongs to ParkBranch, with image + status)

```bash
curl -sS -X POST "$BASE/api/games" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "parkBranchId": 1,
    "name": "Bumper Cars",
    "description": "Classic bumper car arena.",
    "imageUrl": "https://cdn.example.com/games/bumper-cars.jpg",
    "status": true
  }'
```

List / by branch:

```bash
curl -sS "$BASE/api/games"
curl -sS "$BASE/api/games?branchId=1"
```

---

### 3) Create a Branch Amenity (now can link to Game; image + status)

> Requires an existing **AmenityType** (assume id=1).

```bash
curl -sS -X POST "$BASE/api/branch-amenities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "parkBranchId": 1,
    "amenityTypeId": 1,
    "gameId": 1,
    "name": "Arcade Zone",
    "description": "Retro arcade section near the bumper cars.",
    "imageUrl": "https://cdn.example.com/amenities/arcade.jpg",
    "status": true
  }'
```

List / by branch:

```bash
curl -sS "$BASE/api/branch-amenities"
curl -sS "$BASE/api/branch-amenities?branchId=1"
```

---

### 4) Create a Branch Voucher (renamed from Promotion)

If you **haven’t** renamed controller yet, replace `/api/branch-vouchers` with `/api/branch-promotions` and keep body identical; the entity/table should now be `branch_voucher`.

```bash
curl -sS -X POST "$BASE/api/branch-vouchers" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "parkBranchId": 1,
    "description": "Midweek 10% off",
    "discountType": "PERCENTAGE",   // or "FIXED_AMOUNT"
    "discountValue": 10,
    "validFrom": "2025-10-01T00:00:00",
    "validUntil": "2025-10-31T23:59:59",
    "imageUrl": "https://cdn.example.com/vouchers/midweek.png",
    "status": true
  }'
```

List / by branch:

```bash
curl -sS "$BASE/api/branch-vouchers"
curl -sS "$BASE/api/branch-vouchers?branchId=1"
```

---

### 5) Create a Shift (defaults 10:00–22:00; add working days)

```bash
curl -sS -X POST "$BASE/api/shifts" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "startTime": "10:00:00",
    "endTime": "22:00:00",
    "daysOfWeek": "MON,TUE,WED,THU,FRI,SAT,SUN",
    "description": "Default shift"
  }'
```

List:

```bash
curl -sS "$BASE/api/shifts"
```

---

### 6) Create a Staff Assignment (scan in/out timestamps + status)

> Requires an existing **BranchStaff** (assume id=1) and **Shift** (id=1)

```bash
curl -sS -X POST "$BASE/api/staff-assignments" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "assignedDate": "2025-10-05",
    "branchStaffId": 1,
    "shiftId": 1,
    "scanInAt": "2025-10-05T10:03:11",
    "scanOutAt": "2025-10-05T22:01:42",
    "status": true
  }'
```

List (for a user) — whichever your controller supports:

```bash
# If you implemented query by user id:
curl -sS "$BASE/api/staff-assignments?userId=1"

# Or nested under users:
curl -sS "$BASE/api/users/1/staff-assignments"
```

---

### 7) “Get all … of Branch” checks

(Use whichever style you implemented — query param or nested resources.)

```bash
# Reviews of branch
curl -sS "$BASE/api/branch-reviews?branchId=1"
# or
curl -sS "$BASE/api/park-branches/1/reviews"

# Staff of branch
curl -sS "$BASE/api/branch-staff?branchId=1"
# or
curl -sS "$BASE/api/park-branches/1/staff"

# Vouchers of branch
curl -sS "$BASE/api/branch-vouchers?branchId=1"
# or
curl -sS "$BASE/api/park-branches/1/vouchers"

# Amenities of branch
curl -sS "$BASE/api/branch-amenities?branchId=1"
# or
curl -sS "$BASE/api/park-branches/1/amenities"

# Games of branch
curl -sS "$BASE/api/games?branchId=1"
# or
curl -sS "$BASE/api/park-branches/1/games"
```

---

### 8) Toggle `status` (soft disable) for common entities

```bash
# Disable a game
curl -sS -X PATCH "$BASE/api/games/1/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"status": false}'

# Disable an amenity
curl -sS -X PATCH "$BASE/api/branch-amenities/1/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"status": false}'

# Disable a voucher
curl -sS -X PATCH "$BASE/api/branch-vouchers/1/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"status": false}'

# Disable a branch
curl -sS -X PATCH "$BASE/api/park-branches/1/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"status": false}'
```

---

### Notes / quick sanity checklist

* If you haven’t created **AmenityType** and **BranchStaff** seeds, do that first (or temporarily null those FKs if you made them nullable).
* If your controllers still use **Promotion** naming, just hit `/api/branch-promotions` until you finish the rename.
* `LocalTime` fields must be `"HH:mm:ss"` strings in JSON (e.g., `"10:00:00"`).
* IDs in the sample (`1`) are placeholders; adjust to what your DB returns.
