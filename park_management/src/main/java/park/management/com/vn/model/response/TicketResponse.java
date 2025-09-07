package park.management.com.vn.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// NEW: import the pass-link DTO
// (Create class: park.management.com.vn.model.response.TicketPassLink)
@NoArgsConstructor
@Data
public class TicketResponse {
  private Long ticketId;
  private String status;
  private List<TicketDetailResponse> details;

  // NEW: list of QR links (one per unit)
  private List<TicketPassLink> passes;
}
