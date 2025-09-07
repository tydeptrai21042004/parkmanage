package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import lombok.Data;

@Builder
@Setter
@Getter
@lombok.Data
public class ParkBranchRequest {

    @NotBlank
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String location;

    @NotNull
    private LocalDateTime open;

    @NotNull
    private LocalDateTime close;

}
