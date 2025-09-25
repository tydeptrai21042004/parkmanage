ok, làm theo checklist dưới đây là xong. Mình đưa code mẫu “điển hình” cho từng nhóm để bạn copy vào đúng file trong project của bạn. Có chỗ mình chỉ ra các dòng cần thêm/sửa; bạn search đúng file để dán vào.

---

# 0) Checklist nhanh

1. **Đưa field mới (status, imageUrl, …) vào các *Request DTO***: `GameRequest`, `BranchAmenityRequest`, `TicketTypeRequest`, `EventRequest`, … để cập nhật được từ API.
2. **TicketType quan hệ `ParkBranch` (KHÔNG phải Game)**: sửa entity + request + repo + controller.
3. **Xoá “BulkPricingRule” khỏi tính giá** (giảm giá dùng Voucher): sửa `TicketServiceImpl`.
4. **Giữ “DailyTicketInventory”** (giải thích ở mục 5) – không cần endpoint riêng nếu chưa dùng; nó được check trong lúc đặt vé.
5. **Thêm các API `getAll...OfBranch`** cho: `ticket-type`, `branch-amenity`, `event`, `branch-review`, `branch-voucher` (tức BranchPromotion), `branch-staff`, `user-entity`.
6. **StaffAssignment filter theo user/tháng/năm** và theo branch/tháng/năm.
7. **API cập nhật ảnh riêng** cho entity có ảnh: `park-branch`, `branch-amenity`, `game` (và `event` nếu bạn lưu ảnh).
8. **Trùng entity Event**: trong source bạn đang có `Event` **và** `BranchEvent`. **Giữ `Event`**, xoá `BranchEvent` (entity + repo + service + controller liên quan).

---

# 1) Cập nhật Request DTO để có `status` (và `imageUrl` nơi cần)

## `model/request/GameRequest.java`

```java
package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GameRequest {
  @NotNull
  private Long branchId;
  @NotNull
  private String name;
  private String description;

  // field mới
  private String imageUrl;   // nếu bạn muốn lưu ảnh cho game
  private Boolean status;    // enable/disable
}
```

## `model/request/BranchAmenityRequest.java`

```java
package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BranchAmenityRequest {
  @NotNull
  private Long parkBranchId;
  @NotNull
  private Long amenityTypeId;
  @NotNull
  private String name;
  private String description;

  // field mới
  private String imageUrl;
  private Boolean status;
}
```

## `model/request/TicketTypeRequest.java`

```java
package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TicketTypeRequest {
  @NotNull
  private Long parkBranchId;       // <--- CHÍNH: FK sang branch
  @NotNull
  private String name;
  @NotNull
  private BigDecimal basePrice;
  private String description;

  // field mới
  private Boolean status;
}
```

## `model/request/EventRequest.java`

```java
package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EventRequest {
  @NotNull private Long parkBranchId;
  @NotNull private String name;
  private String description;
  @NotNull private LocalDateTime startAt;
  @NotNull private LocalDateTime endAt;

  // field mới
  private String imageUrl;
  private Boolean status;
}
```

> Làm tương tự (thêm `status`/`imageUrl` nếu có) cho các DTO khác bạn muốn cập nhật (vd: `BranchStaffRequest`, `BranchReviewRequest`, …).

---

# 2) TicketType quan hệ với ParkBranch (không phải Game)

## `entity/TicketType.java` (sửa)

```java
@Entity
@Table(name = "ticket_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketType extends BaseEntity {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "park_branch_id", nullable = false)   // <--- FK sang branch
  private ParkBranch parkBranch;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, precision = 18, scale = 0)
  private BigDecimal basePrice;

  @Column(length = 2000)
  private String description;

  // mới
  @Column(nullable = false)
  private Boolean status = true;
}
```

## `repository/TicketTypeRepository.java`

```java
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
  List<TicketType> findByParkBranch_Id(Long branchId);
}
```

## `controller/TicketTypeController.java` (thêm endpoint of-branch)

```java
@GetMapping("/of-branch/{branchId}")
public ResponseEntity<List<TicketTypeResponse>> getAllOfBranch(@PathVariable Long branchId) {
  List<TicketType> list = ticketTypeRepository.findByParkBranch_Id(branchId);
  return ResponseEntity.ok(
      list.stream().map(mapper::toResponse).toList()
  );
}
```

> Đừng quên trong service khi tạo/sửa `TicketType` phải set `parkBranch` theo `request.getParkBranchId()`.

---

# 3) Bỏ hoàn toàn BulkPricingRule khỏi tính giá

## Sửa `service/impl/TicketServiceImpl.java`

* **Xoá** các field:

```java
// private final BulkPricingRuleRepository bulkPricingRuleRepository;
```

* **Xoá** import và mọi chỗ gọi `findBulkPricingRuleByTicketTypeId(...)`, `BulkPricingRule`, `discountPercent`…

* **Tính tiền** = `sum(basePrice * quantity)` rồi trừ **promotion/voucher** (nếu có). Ví dụ thay đoạn định giá từng loại:

```java
for (Map.Entry<TicketType, Integer> entry : ticketTypeQuantityMap.entrySet()) {
  TicketType tt = entry.getKey();
  int qty = entry.getValue();
  BigDecimal priceForType = tt.getBasePrice().multiply(BigDecimal.valueOf(qty));
  ticketTypePriceMap.put(tt, priceForType);
}
```

* Khi build `TicketDetail`:

```java
TicketDetail ticketDetail = TicketDetail.builder()
    .ticketOrder(savedOrder)
    .ticketType(ticketType)
    .quantity(quantity)
    .unitPrice(unitPrice)            // = tt.getBasePrice()
    .discountPercent(0)              // bỏ bulk rule => 0
    .finalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity))
                 .setScale(0, RoundingMode.HALF_EVEN))
    .build();
```

> Sau khi sửa, bạn có thể **xoá** entity/repo/service nào liên quan `BulkPricingRule` nếu không dùng ở nơi khác.

---

# 4) Các API `getAll...OfBranch`

Thêm method repo & controller tương tự như `TicketType` ở trên:

## Repositories

```java
// BranchAmenity
List<BranchAmenity> findByParkBranch_Id(Long branchId);

// Event
List<Event> findByParkBranch_Id(Long branchId);

// BranchReview
List<BranchReview> findByParkBranch_Id(Long branchId);

// BranchPromotion (branch-voucher)
List<BranchPromotion> findByParkBranch_Id(Long branchId);

// BranchStaff
List<BranchStaff> findByParkBranch_Id(Long branchId);

// UserEntity (nếu có FK)
List<UserEntity> findByParkBranch_Id(Long branchId);
```

## Controllers

Thêm endpoint:

```java
@GetMapping("/of-branch/{branchId}")
public ResponseEntity<List<...Response>> ofBranch(@PathVariable Long branchId) { ... }
```

> Map sang response như bạn đang làm.

---

# 5) “DailyTicketInventory” dùng để làm gì?

* Đây là **bảng tồn kho theo ngày** cho từng `TicketType`:

  * `ticketType_id`, `date`, `totalAvailable`, `sold`
* Khi tạo order, service **đọc nó để kiểm tra còn chỗ không** (không oversell) rồi **cộng sold** tương ứng.
* Nó **không thuộc một controller khách dùng trực tiếp**; thường là **admin** setup capacity theo ngày. Nếu cần bạn có thể thêm admin API:

  * `GET /api/inventories?ticketTypeId=&date=`
  * `PUT /api/inventories/{id}` để chỉnh `totalAvailable`.

Hiện tại, `TicketServiceImpl` đang **dùng trực tiếp** repo `DailyTicketInventoryRepository` để enforce capacity — bạn **giữ nguyên** là đủ.

---

# 6) StaffAssignment filter theo user/tháng/năm & branch/tháng/năm

## `repository/StaffAssignmentRepository.java`

```java
public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {

  // Nếu BranchStaff có userEntity:
  @Query("""
    select sa from StaffAssignment sa
      join sa.staff s
      join s.userEntity u
     where u.id = :userId
       and sa.assignedDate between :start and :end
  """)
  List<StaffAssignment> findAllOfUserInMonth(@Param("userId") Long userId,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);

  @Query("""
    select sa from StaffAssignment sa
      join sa.staff s
      join s.parkBranch b
     where b.id = :branchId
       and sa.assignedDate between :start and :end
  """)
  List<StaffAssignment> findAllOfBranchInMonth(@Param("branchId") Long branchId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);
}
```

## `controller/StaffAssignmentController.java` (thêm 2 API)

```java
@GetMapping("/of-user")
public ResponseEntity<List<StaffAssignmentResponse>> ofUser(
    @RequestParam Long userId,
    @RequestParam int month,
    @RequestParam int year) {

  LocalDate start = LocalDate.of(year, month, 1);
  LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
  var list = repo.findAllOfUserInMonth(userId, start, end);
  return ResponseEntity.ok(list.stream().map(mapper::toResponse).toList());
}

@GetMapping("/of-branch/{branchId}")
public ResponseEntity<List<StaffAssignmentResponse>> ofBranch(
    @PathVariable Long branchId,
    @RequestParam int month,
    @RequestParam int year) {

  LocalDate start = LocalDate.of(year, month, 1);
  LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
  var list = repo.findAllOfBranchInMonth(branchId, start, end);
  return ResponseEntity.ok(list.stream().map(mapper::toResponse).toList());
}
```

---

# 7) API cập nhật ảnh riêng

Tạo 1 DTO dùng chung:

## `model/request/UpdateImageRequest.java`

```java
package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateImageRequest {
  @NotBlank
  private String imageUrl;
}
```

## Thêm endpoint cho từng controller

### `ParkBranchController`

```java
@PutMapping("/{id}/image")
public ResponseEntity<Void> updateImage(@PathVariable Long id,
                                        @RequestBody @Valid UpdateImageRequest req) {
  ParkBranch b = parkBranchRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));
  b.setImageUrl(req.getImageUrl()); // đảm bảo entity có field imageUrl
  parkBranchRepository.save(b);
  return ResponseEntity.noContent().build();
}
```

### `BranchAmenityController`

```java
@PutMapping("/{id}/image")
public ResponseEntity<Void> updateImage(@PathVariable Long id,
                                        @RequestBody @Valid UpdateImageRequest req) {
  BranchAmenity a = branchAmenityRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("AMENITY_NOT_FOUND"));
  a.setImageUrl(req.getImageUrl()); // thêm field imageUrl trong entity nếu chưa có
  branchAmenityRepository.save(a);
  return ResponseEntity.noContent().build();
}
```

### `GameController`

```java
@PutMapping("/{id}/image")
public ResponseEntity<Void> updateImage(@PathVariable Long id,
                                        @RequestBody @Valid UpdateImageRequest req) {
  Game g = gameRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("GAME_NOT_FOUND"));
  g.setImageUrl(req.getImageUrl()); // thêm field imageUrl trong Game nếu chưa có
  gameRepository.save(g);
  return ResponseEntity.noContent().build();
}
```

> Nếu `Event` cũng có ảnh, làm tương tự.

---

# 8) Trùng `Event` vs `BranchEvent`

* Bạn đã có `entity/Event.java` + `EventController/Service/Repo`.
* **Xoá** `BranchEvent.java` và mọi repo/service/controller dùng nó (nếu còn).
* Đảm bảo mọi tham chiếu chuyển sang `Event`.

---

# 9) cURL test nhanh

### TicketType of Branch

```bash
curl -s http://localhost:8080/api/ticket-types/of-branch/1
```

### Branch Amenity of Branch

```bash
curl -s http://localhost:8080/api/branch-amenities/of-branch/1
```

### Event of Branch

```bash
curl -s http://localhost:8080/api/events/of-branch/1
```

### StaffAssignment of User (tháng 9/2025)

```bash
curl -s "http://localhost:8080/api/staff-assignments/of-user?userId=5&month=9&year=2025"
```

### StaffAssignment of Branch (tháng 9/2025)

```bash
curl -s "http://localhost:8080/api/staff-assignments/of-branch/1?month=9&year=2025"
```

### Update ảnh cho Game

```bash
curl -X PUT http://localhost:8080/api/games/10/image \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://cdn.example.com/img/game10.jpg"}'
```

---

Nếu bạn muốn, mình có thể dán thêm **mẫu sửa entity** để thêm `imageUrl`/`status` cho `Game`, `BranchAmenity`, `ParkBranch`, `Event`, và **mẫu mapper** cập nhật các field mới vào `*Response`.
