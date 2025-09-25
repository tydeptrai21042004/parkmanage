package park.management.com.vn.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TicketRequest {

    @Valid
    @NotEmpty
    private List<TicketDetailRequest> details;

    private Long branchId;

    private Long promotionId;

    @NotNull
    @FutureOrPresent
    private LocalDate ticketDate;

    // ===== NEW: guest checkout fields (optional to not break logged-in flow) =====
    private String customerName;
    private Integer customerAge;
    private String customerEmail;
    private String customerPhone;

    // Optional: wire vouchers later (§7)
    private String voucherCode;

    // inner static class
    @Data
    public static class TicketDetailRequest {
        @NotNull
        private Long ticketTypeId;

        @NotNull
        @Min(1)
        private Integer quantity;

        private Long promotionId; // optional
    }
}
