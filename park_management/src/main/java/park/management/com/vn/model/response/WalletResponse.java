package park.management.com.vn.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor          // public no-args ctor so MapStruct can instantiate and use setters
@AllArgsConstructor         // public all-args ctor in case MapStruct prefers constructor mapping
public class WalletResponse {

    private Long id;
    private double balance; // keep as double to match your current mapping
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
