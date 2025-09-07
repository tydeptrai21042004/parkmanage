package park.management.com.vn.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class TicketDetailResponse {
    private Long ticketTypeId;
    private String ticketTypeName;
    private LocalDate ticketDate;

    private Integer quantity;
    private BigDecimal price;
    private Integer discount;
    private BigDecimal finalPrice;
}