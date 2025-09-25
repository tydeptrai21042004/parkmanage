package park.management.com.vn.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationFilter authenticationFilter;
  private final PermissionEvaluatorImpl permissionEvaluator;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    log.info("(securityFilterChain) start");

    http
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // CORS preflight
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // WebSocket handshake/info
        .requestMatchers("/ws/**").permitAll()

        // Swagger / OpenAPI
        .requestMatchers(
          "/swagger-ui.html",
          "/swagger-ui/**",
          "/v3/api-docs",
          "/v3/api-docs/**",
          "/v3/api-docs.yaml"
        ).permitAll()

        // Public endpoints (login/register + payment callbacks)
        .requestMatchers(
          "/public/**",
          "/api/users/login",
          "/api/users/register",
          "/api/payment/payos/webhook",
          "/api/local/topups/confirm",
          "/api/payment/payos/webhook/dev-complete/**"
        ).permitAll()

        // *** Public catalog & guest checkout ***
        .requestMatchers(HttpMethod.GET,  "/api/ticket-types/**").permitAll()
        .requestMatchers(HttpMethod.GET,  "/api/events/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()

        // TEMP (until /api/orders is implemented): allow guest on legacy ticket creation
        .requestMatchers(HttpMethod.POST, "/api/tickets").permitAll()

        // Ticket types admin
        .requestMatchers(HttpMethod.POST,   "/api/ticket-types/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(HttpMethod.PUT,    "/api/ticket-types/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/ticket-types/**").hasAnyRole("MANAGER","ADMIN")

        // Events admin
        .requestMatchers(HttpMethod.POST,   "/api/events/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(HttpMethod.PUT,    "/api/events/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasAnyRole("MANAGER","ADMIN")

        // Orders (other ops require auth)
        .requestMatchers(HttpMethod.GET,    "/api/orders/**").authenticated()
        .requestMatchers(HttpMethod.PUT,    "/api/orders/**").authenticated()
        .requestMatchers(HttpMethod.DELETE, "/api/orders/**").authenticated()

        // Ticket pass endpoints
        .requestMatchers(HttpMethod.GET,  "/api/passes/*").permitAll()        // public QR check
        .requestMatchers(HttpMethod.POST, "/api/passes/*/redeem").hasAuthority("TICKET_VALIDATE")

        // Manager scope
        .requestMatchers("/api/manager/**").hasRole("MANAGER")

        // Games & game reviews
        .requestMatchers(HttpMethod.GET,    "/api/games/**").authenticated()
        .requestMatchers(HttpMethod.POST,   "/api/games/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(HttpMethod.PUT,    "/api/games/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/games/**").hasAnyRole("MANAGER","ADMIN")

        .requestMatchers(HttpMethod.POST, "/api/game-reviews").authenticated()
        .requestMatchers(HttpMethod.GET,  "/api/game-reviews/**").authenticated()

        // Notification admin
        .requestMatchers(HttpMethod.POST,   "/api/notifications/**", "/api/notification/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT,    "/api/notifications/**", "/api/notification/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/notifications/**", "/api/notification/**").hasRole("ADMIN")
        .requestMatchers(org.springframework.http.HttpMethod.GET,  "/api/ticket-types/**").permitAll()
        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/ticket-types/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(org.springframework.http.HttpMethod.PUT,  "/api/ticket-types/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(org.springframework.http.HttpMethod.DELETE,"/api/ticket-types/**").hasAnyRole("MANAGER","ADMIN")
        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
        .requestMatchers("/api/orders/**").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/orders/*/refund").authenticated()
        .requestMatchers(HttpMethod.GET , "/api/orders/*/refund").authenticated()
        // View notifications
        .requestMatchers(HttpMethod.GET, "/api/notifications/**", "/api/notification/**").authenticated()

        // Everything else requires JWT
        .anyRequest().authenticated()
      );

    http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public MethodSecurityExpressionHandler createExpressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(permissionEvaluator);
    return expressionHandler;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOriginPatterns(List.of(
      "http://localhost:5173",
      "http://127.0.0.1:5173"
    ));
    // FIX: removed stray comma after POST
    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS","HEAD"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setExposedHeaders(List.of("Authorization", "Location"));
    cfg.setAllowCredentials(true);
    cfg.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
