package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class AIChatRequest {
  @NotBlank
  private String prompt;
  private List<String> history;   // đơn giản: mảng tin nhắn trước (optional)
}
