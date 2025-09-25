Perfect—thanks for pasting the **actual** Swagger. I’ve remapped the earlier plan so it **matches your current API exactly** (controllers/tags that already exist, and what needs adding). Use this as your to-do list.

---

# A) Security: public until checkout

You already whitelisted login/register, passes, PayOS…
Add/keep these (some are *future* routes you’ll create):

* `GET /api/ticket-types/**` → **permitAll**
* `GET /api/events/**` → **permitAll**
* `POST /api/orders` → **permitAll** (guest checkout)
* (While you haven’t created `/api/orders` yet, you can temporarily **permitAll** `POST /api/tickets` so guest checkout works with the old route.)

---

# B) Where each feature lands in **your** API

## 1) “Không cần đăng nhập” (xem vé/sự kiện + tạo order)

* **SecurityConfig.java**: as above.
* **AuthenticationFilter**: OK as-is (only authenticates if token exists).

## 2) Thông tin khách (tên/tuổi/email/sđt) khi mua (guest)

You currently have only:

* `ticket-controller` → `POST /api/tickets` with body `TicketRequest` (has `details`, `branchId`, `promotionId`, `ticketDate`)

Two options (pick ONE):

**(A) Extend existing “tickets” API (fastest, no new controller now)**

* **Edit**

  * `model/request/TicketRequest.java` → add

    * `String customerName; Integer customerAge; String customerEmail; String customerPhone;`
    * `String voucherCode;` *(optional, see §7)*
  * `entity/TicketOrder.java` → add guest fields above (+ `Voucher` fields later)
  * `service/impl/TicketServiceImpl.java` (or wherever `POST /api/tickets` is handled) → validate guest data when `userId == null`.
* **SecurityConfig**: `POST /api/tickets` → **permitAll** (temporary until `/api/orders` lands).

**(B) Create proper “orders” API (recommended)**

* **Create**

  * `controller/OrderController.java` ⇒ tag: `order-controller`
  * `model/request/CreateOrderRequest.java` (items + guest info + voucher + event/use date)
  * `model/response/OrderResponse.java`
  * `service/OrderService.java` + `impl/OrderServiceImpl.java`
* **Edit**

  * `entity/TicketOrder.java` → add guest fields.
* **SecurityConfig**: `POST /api/orders` → **permitAll**

> You can keep `/api/tickets` calling into the new `OrderService` for backward compatibility and mark it “deprecated” in Swagger.

## 3) Event CRUD (tên, mô tả, thời gian, chi nhánh)

**You don’t have events yet. Add:**

* **Create**

  * `entity/Event.java` (name, description, startAt, endAt, `@ManyToOne ParkBranch`)
  * `repository/EventRepository.java`
  * `service/EventService.java` + `impl/EventServiceImpl.java`
  * `model/request/EventRequest.java` + `model/response/EventResponse.java`
  * `controller/EventController.java` (tag: `event-controller`)

    * `GET /api/events`
    * `GET /api/events/{id}`
    * `POST /api/events`
    * `PUT /api/events/{id}`
    * `DELETE /api/events/{id}`
* **SecurityConfig**: `GET /api/events/**` → **permitAll**

## 4) Ticket Type CRUD

**Not present in your Swagger. Add:**

* **Create**

  * `controller/TicketTypeController.java` (tag: `ticket-type-controller`)

    * `GET /api/ticket-types`
    * `GET /api/ticket-types/{id}`
    * `POST /api/ticket-types`
    * `PUT /api/ticket-types/{id}`
    * `DELETE /api/ticket-types/{id}`
  * `model/request/TicketTypeRequest.java`, `model/response/TicketTypeResponse.java`
  * (Use your existing `TicketType`/`TicketTypeRepository` if present; if not, create entity+repo)
* **SecurityConfig**:

  * `GET /api/ticket-types/**` → **permitAll**
  * `POST|PUT|DELETE /api/ticket-types/**` → `hasAnyRole("MANAGER","ADMIN")`

## 5) Orders API (tạo/lấy/list/cập nhật/hủy)

**Not present. Add:**

* **Create**

  * `controller/OrderController.java` (tag: `order-controller`)

    * `POST   /api/orders` *(guest allowed)*
    * `GET    /api/orders/{id}`
    * `GET    /api/orders?userId=...`
    * `PUT    /api/orders/{id}` *(paid | canceled | refunded)*
    * `DELETE /api/orders/{id}` *(cancel if unpaid per policy)*
  * `model/request/UpdateOrderStatusRequest.java`
  * (Reuse `CreateOrderRequest`, `OrderResponse` from §2B)
  * `service/OrderService.java` + `impl/OrderServiceImpl.java`
  * `repository/TicketOrderRepository.java` → `List<TicketOrder> findByUserId(Long userId);`
* **Business rules in `OrderServiceImpl`**

  * Quantity per line ≤ 10
  * Time rule (before 15:00 same-day) / or bound to Event slot
  * Daily/slot inventory (use your `DailyTicketInventory*`)
* **SecurityConfig**

  * `POST /api/orders` → **permitAll**
  * `GET|PUT|DELETE /api/orders/**` → `authenticated()`

## 6) Refund

You already have `OrderRefund` entity/repo. Add endpoints:

* **Create**

  * `controller/RefundController.java` (or put in `OrderController`, tag: `order-refund-controller`)

    * `POST /api/orders/{orderId}/refund`
    * `GET  /api/orders/{orderId}/refund`
  * `model/request/RefundRequest.java` (+ optional `RefundResponse`)
  * `service/RefundService.java` + `impl/RefundServiceImpl.java`
* **Rules**

  * ≥ 24h before use time; not checked-in; no multiple refunds; refund amount = discounted price
  * If paid via PayOS → call refund or mark pending
* **Edit**

  * read check-in from `ticket-pass-controller` flow (`/api/passes/{code}/redeem`) or add `checkedIn` to `TicketDetail`.

## 7) promotion (1 voucher/đơn;  ≤50% & ≤500k; còn hạn; mỗi KH ≤3 lần cùng mã)

Not present. Add:

* **Create**

  * `entity/Voucher.java` (code unique, percent ≤ 0.5, maxDiscount ≤ 500000, startAt/endAt, active)
  * `entity/VoucherUsage.java` (voucher, userId or guestEmail, usedAt, orderId)
  * `repository/VoucherRepository.java`
  * `repository/VoucherUsageRepository.java`
  * `service/VoucherService.java` + `impl/VoucherServiceImpl.java`
  * (Optional) `controller/VoucherController.java` for admin CRUD
* **Edit**

  * `entity/TicketOrder.java`: `@ManyToOne Voucher voucher; BigDecimal discountAmount;`
  * `OrderServiceImpl.createOrder()` → call `VoucherService.validateAndPrice(...)` and enforce **only 1 voucher/order**.
  * Count usages to enforce “≤ 3 per customer per code”.

## 8) Notification + Email on purchase/top-up

You already have app notifications:

* **Edit**

  * `service/impl/OrderServiceImpl.java`: after `PAID` → push app notification + send email
  * `service/impl/WalletTopupServiceImpl.java`: after success → notify + email
* **Add **

  * `service/EmailService.java` + `impl/EmailServiceImpl.java`
  * `application.yml` SMTP config

## 9) PayOS webhook → Order

You have:

* `pay-os-webhook-controller` & `pay-os-webhook-dev-controller`
* **Edit**

  * Map `orderCode/paymentLinkId` → `TicketOrder`
  * On PAID:

    * `order.status = PAID`
    * persist `VoucherUsage`
    * notifications + inventory finalize/hold release

## 10) Rule “trước 15:00”

* **Implement in** `OrderServiceImpl.createOrder()`:

  * If `useDate == today` and `now >= 15:00` → reject
  * Or force valid `Event` slot selection
* **If needed** add `useDate`/`slotId` to `TicketOrder` or `TicketDetail`.

---

# C) Swagger updates to mirror **your** API

Add these **new tags and paths** (keep your existing ones):

### `event-controller`

```
GET    /api/events
GET    /api/events/{id}
POST   /api/events
PUT    /api/events/{id}
DELETE /api/events/{id}
```

### `ticket-type-controller`

```
GET    /api/ticket-types
GET    /api/ticket-types/{id}
POST   /api/ticket-types
PUT    /api/ticket-types/{id}
DELETE /api/ticket-types/{id}
```

### `order-controller`

```
POST   /api/orders                 // guest allowed
GET    /api/orders/{id}
GET    /api/orders?userId=...
PUT    /api/orders/{id}            // paid | canceled | refunded
DELETE /api/orders/{id}            // cancel if unpaid
```

### `order-refund-controller` (or under order-controller)

```
POST   /api/orders/{orderId}/refund
GET    /api/orders/{orderId}/refund
```

### (optional) `voucher-controller`

```
GET    /api/vouchers
GET    /api/vouchers/{id}
POST   /api/vouchers
PUT    /api/vouchers/{id}
DELETE /api/vouchers/{id}
```

### **Schemas to add/extend**

* `EventRequest`, `EventResponse`
* `TicketTypeRequest`, `TicketTypeResponse`
* `CreateOrderRequest` *(or extend your existing `TicketRequest`)*:

  * `items[]: { ticketTypeId, quantity }`
  * `customerName, customerAge, customerEmail, customerPhone`
  * `voucherCode`
  * `useDate` or `eventId`
* `OrderResponse`
* `UpdateOrderStatusRequest`
* `RefundRequest`, `RefundResponse`
* `Voucher`, `VoucherRequest/Response`

> If you keep using `/api/tickets` for creation right now, **extend `TicketRequest`** with the guest+voucher fields, and mark it deprecated once `/api/orders` is live.

---

## D) Quick smoke tests (cURL)

**Public catalog (once added)**

```bash
curl -i http://localhost:8080/api/ticket-types
curl -i http://localhost:8080/api/events
```

**Guest checkout**

```bash
# If you extended POST /api/tickets
curl -i -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"details":[{"ticketTypeId":1,"quantity":2}],
       "branchId":1,"ticketDate":"2025-09-21",
       "customerName":"Dang Ty","customerAge":21,
       "customerEmail":"ty@example.com","customerPhone":"+84912345678",
       "voucherCode":"WELCOME50"}'

# If you created POST /api/orders
curl -i -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"items":[{"ticketTypeId":1,"quantity":2}],
       "eventId":10,
       "customerName":"Dang Ty","customerAge":21,
       "customerEmail":"ty@example.com","customerPhone":"+84912345678",
       "voucherCode":"WELCOME50"}'
```

**Refund**

```bash
curl -i -X POST http://localhost:8080/api/orders/123/refund \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"reason":"Change of plans"}'

curl -i -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/orders/123/refund
```

---

## E) File checklist tailored to your repo

```
config/SecurityConfig.java                // public routes
config/AuthenticationFilter.java          // keep as-is

// NEW
entity/Event.java
repository/EventRepository.java
service/EventService.java
service/impl/EventServiceImpl.java
model/request/EventRequest.java
model/response/EventResponse.java
controller/EventController.java           // /api/events

controller/TicketTypeController.java      // /api/ticket-types
model/request/TicketTypeRequest.java
model/response/TicketTypeResponse.java

// EITHER extend tickets or add orders:
model/request/TicketRequest.java          // (extend)  OR
controller/OrderController.java           // /api/orders
model/request/CreateOrderRequest.java
model/response/OrderResponse.java
model/request/UpdateOrderStatusRequest.java
service/OrderService.java
service/impl/OrderServiceImpl.java
repository/TicketOrderRepository.java     // +findByUserId

// Refund
controller/RefundController.java          // or in OrderController
model/request/RefundRequest.java
service/RefundService.java
service/impl/RefundServiceImpl.java

// Voucher
entity/Voucher.java
entity/VoucherUsage.java
repository/VoucherRepository.java
repository/VoucherUsageRepository.java
service/VoucherService.java
service/impl/VoucherServiceImpl.java
// (optional) controller/VoucherController.java

// Edits
entity/TicketOrder.java                   // +guest fields +voucher link
service/impl/WalletTopupServiceImpl.java  // notify/email after topup
controller/PayOSWebhookController.java    // map PAID → order
resources/application.yml                 // SMTP (if emailing)
```

If you want, I can generate **exact Swagger YAML snippets** for `/api/events`, `/api/ticket-types`, `/api/orders`, and `/api/orders/{id}/refund` so they drop straight into your `v3/api-docs`.
