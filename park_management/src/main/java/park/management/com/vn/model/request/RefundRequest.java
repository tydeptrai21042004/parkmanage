package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RefundRequest {
    @NotBlank
    private String reason;
}
