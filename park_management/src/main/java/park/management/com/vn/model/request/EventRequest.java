package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

  // The service currently calls request.getBranchId().
  // Keep the canonical field as parkBranchId, and provide a convenience getter getBranchId()
  // so existing code (EventServiceImpl) compiles without changes.
  @NotNull
  private Long parkBranchId;

  @NotNull
  private String name;

  private String description;

  @NotNull
  private LocalDateTime startAt;

  @NotNull
  private LocalDateTime endAt;

  // optional new fields
  private String imageUrl;
  private Boolean status;

  // convenience for existing service code
  public Long getBranchId() {
    return this.parkBranchId;
  }
}
