package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketTypeResponse {
  private Long id;
  private String name;
  private String description;
  private BigDecimal basePrice;

  // from BaseEntity (if you want them in the response)
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
