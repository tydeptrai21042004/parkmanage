package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Builder
@Data
public class ParkBranchRequest {

    @NotBlank
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String location;

    @NotNull
    private LocalTime openTime;

    @NotNull
    private LocalTime closeTime;
}
