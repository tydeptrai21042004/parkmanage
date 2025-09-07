package park.management.com.vn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.genai.Client;

@Configuration
public class GeminiConfig {

  @Bean
  public Client geminiClient(@Value("${gemini.api-key:}") String apiKey) {
    // Nếu có key trong YAML/env -> set; nếu không, SDK tự dùng GOOGLE_API_KEY.
    if (apiKey != null && !apiKey.isBlank()) {
      return Client.builder().apiKey(apiKey).build();
    }
    return new Client(); // dùng GOOGLE_API_KEY từ env
  }
}
