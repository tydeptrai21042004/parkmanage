package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.AIChatRequest;
import park.management.com.vn.model.response.AIChatResponse;
import park.management.com.vn.security.UserPrincipal;
import park.management.com.vn.service.AIChatService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIChatController {

  private final AIChatService service;

  private Long currentUserId(Authentication auth) {
    if (auth != null && auth.getPrincipal() instanceof UserPrincipal up) return up.getId();
    return null;
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/chat")
  public ResponseEntity<AIChatResponse> chat(@Valid @RequestBody AIChatRequest req, Authentication auth) {
    return ResponseEntity.ok(service.chat(currentUserId(auth), req));
  }
}
