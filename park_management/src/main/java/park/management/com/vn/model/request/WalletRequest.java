package park.management.com.vn.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.math.BigDecimal;

@Builder
@Setter
@Getter
@lombok.Data
public class WalletRequest {

    private Long userId;

    @DecimalMin(value = "0.0", message = "Balance must be a positive number")
    private BigDecimal balance;
}
