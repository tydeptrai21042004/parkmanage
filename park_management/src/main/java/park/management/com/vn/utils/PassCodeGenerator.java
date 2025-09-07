package park.management.com.vn.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class PassCodeGenerator {
  private final SecureRandom rnd = new SecureRandom();
  private static final String ALPH = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  public String newCode(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append(ALPH.charAt(rnd.nextInt(ALPH.length())));
    return sb.toString();
  }
}
