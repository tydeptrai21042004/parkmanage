package park.management.com.vn.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import park.management.com.vn.entity.AIChatLog;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.model.request.AIChatRequest;
import park.management.com.vn.model.response.AIChatResponse;
import park.management.com.vn.repository.AIChatLogRepository;
import park.management.com.vn.repository.UserRepository;
import park.management.com.vn.service.AIChatService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

  private final Client geminiClient;
  private final AIChatLogRepository logRepo;
  private final UserRepository userRepo;                 // <- đã inject
  private final ObjectMapper om = new ObjectMapper();

  @Value("${gemini.model:gemini-2.0-flash}")
  private String model;

  @Override
  @Transactional
  public AIChatResponse chat(Long userId, AIChatRequest req) {
    // -------- resolve userId từ SecurityContext khi thiếu --------
    Long uid = (userId != null) ? userId : resolveCurrentUserId();
    if (uid == null) {
      throw new AccessDeniedException("Unauthenticated: cannot resolve userId from SecurityContext");
    }

    // 1) Gọi Gemini
    GenerateContentResponse res =
        geminiClient.models.generateContent(model, req.getPrompt(), null);
    String answer = res.text();

    // 2) Lưu log (TEXT)
    UserEntity userRef = userRepo.getReferenceById(uid); // proxy managed; id phải tồn tại khi flush

    Map<String, Object> payload = new HashMap<>();
    payload.put("model", model);
    payload.put("prompt", req.getPrompt());
    payload.put("answer", answer);
    payload.put("history", req.getHistory());

    AIChatLog log = new AIChatLog();
    log.setUserEntity(userRef);
    try {
      log.setContent(om.writeValueAsString(payload));
    } catch (Exception e) {
      log.setContent("{\"prompt\":" + quote(req.getPrompt()) + ",\"answer\":" + quote(answer) + "}");
    }

    logRepo.saveAndFlush(log); // flush sớm để phát hiện sai id

    // 3) Trả kết quả
    AIChatResponse out = new AIChatResponse();
    out.setAnswer(answer);
    out.setModel(model);
    return out;
  }

  /** Lấy userId từ SecurityContext (JWT). Không phụ thuộc kiểu principal cụ thể. */
  private Long resolveCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return null;
    Object principal = auth.getPrincipal();
    if (principal == null) return null;

    // a) Thử gọi principal.getId() bằng reflection (nếu bạn có UserPrincipal#getId)
    try {
      var m = principal.getClass().getMethod("getId");
      Object v = m.invoke(principal);
      if (v instanceof Number n) return n.longValue();
    } catch (Exception ignore) { /* bỏ qua */ }

    // b) Nếu là UserDetails/String -> lấy username rồi tra DB để ra id
    String username = null;
    if (principal instanceof UserDetails ud) {
      username = ud.getUsername();
    } else if (principal instanceof CharSequence cs) {
      username = cs.toString();
    }
    if (username != null && !"anonymousUser".equals(username)) {
      return userRepo.findByUsername(username).map(UserEntity::getId).orElse(null);
    }
    return null;
  }

  private static String quote(String s) {
    return s == null ? "null" : "\"" + s.replace("\"", "\\\"") + "\"";
  }
}
