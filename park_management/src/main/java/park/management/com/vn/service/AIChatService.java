package park.management.com.vn.service;

import park.management.com.vn.model.request.AIChatRequest;
import park.management.com.vn.model.response.AIChatResponse;

public interface AIChatService {
  AIChatResponse chat(Long userId, AIChatRequest req);
}
