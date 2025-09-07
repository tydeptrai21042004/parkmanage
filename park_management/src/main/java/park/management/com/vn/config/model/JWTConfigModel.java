package park.management.com.vn.config.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Binds properties:
 *   jwt.secret=...
 *   jwt.expire-time=...   (in seconds)
 *
 * Spring’s relaxed binding lets you use either `expire-time` or `expireTime`.
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JWTConfigModel {

  /** HMAC secret (>= 32 chars for HS256). */
  @NotBlank
  private String secret;

  /** Access token lifetime in SECONDS. */
  @NotNull
  private Long expireTime;
}
