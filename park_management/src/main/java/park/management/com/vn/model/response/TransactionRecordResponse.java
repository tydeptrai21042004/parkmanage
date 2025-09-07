package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@lombok.Data
public class TransactionRecordResponse {
    private Long id;
    private double amount;
    private String type;
    private Long walletId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
