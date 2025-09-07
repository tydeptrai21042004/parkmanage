package park.management.com.vn.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import park.management.com.vn.config.model.JWTConfigModel;
import park.management.com.vn.entity.UserEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTTokenUtils {

  private final JWTConfigModel jwtConfigModel;

  private Key signingKey() {
    // JJWT requires at least 256-bit key for HS256 (i.e., secret length >= 32 bytes)
    return Keys.hmacShaKeyFor(jwtConfigModel.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(UserEntity userEntity) {
    log.info("(generateAccessToken) username: {}", userEntity.getUsername());

    Instant now = Instant.now();
    Instant exp = now.plusSeconds(jwtConfigModel.getExpireTime());

    Map<String, Object> claims = new HashMap<>();
    // put roles if you have them; empty list is fine
    claims.put("roles", List.of());

    return Jwts.builder()
        .setSubject(userEntity.getUsername())
        .addClaims(claims)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(signingKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("Expired JWT token", e);
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Invalid JWT token", e);
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public Authentication getAuthentication(String token) {
    Claims claims = getClaims(token);
    String username = claims.getSubject();

    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    List<String> roles = claims.get("roles", List.class);
    if (roles != null) {
      for (String r : roles) {
        authorities.add(new SimpleGrantedAuthority(r));
      }
    }

    User principal = new User(username, "N/A", authorities);
    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }
}
