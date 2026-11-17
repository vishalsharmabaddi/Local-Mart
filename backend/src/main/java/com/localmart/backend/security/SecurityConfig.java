package com.localmart.backend.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())   // uses the corsConfigurationSource bean below
                .csrf(csrf -> csrf.disable())   // token-based REST API, no browser session cookies
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // open for everyone
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        // MUST be above the public "/shops/**" rule, otherwise shop orders become public
                        .requestMatchers(HttpMethod.GET, "/shops/*/orders").hasAnyRole("SHOP_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/products/**", "/shops/**").permitAll()
                        // only shop owners and admins can create shops/products
                        .requestMatchers(HttpMethod.POST, "/shops", "/products").hasAnyRole("SHOP_OWNER", "ADMIN")
                        // only customers place orders
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("CUSTOMER")
                        // machines (load balancer, uptime monitor) must reach health without login
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                        // Spring's internal error page: must be open, otherwise a real 404/500 shows up as 401
                        .requestMatchers("/error").permitAll()
                        // API docs (only exist in dev; disabled in prod)
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // everything else: any logged-in user
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(jsonError(401, "Login required or token invalid")))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jsonError(401, "Login required or token invalid"))
                        .accessDeniedHandler(accessDenied()));
        return http.build();
    }

    // CORS: tells the BROWSER which other websites (origins) may call this API and read the answers
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${localmart.cors.allowed-origins}") List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);   // exact list, never "*" for an API with logins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);   // browser may cache the preflight answer for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // token says  "role": "SHOP_OWNER"  →  Spring authority  "ROLE_SHOP_OWNER"
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("role");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    // 401: who are you? (no token / bad token)
    private AuthenticationEntryPoint jsonError(int status, String message) {
        return (request, response, ex) -> writeJson(response, status, message);
    }

    // 403: we know who you are, but you're not allowed
    private AccessDeniedHandler accessDenied() {
        return (request, response, ex) -> writeJson(response, 403, "You don't have permission for this");
    }

    private void writeJson(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // the secret key used to SIGN and VERIFY tokens (HMAC-SHA256)
    @Bean
    public SecretKey jwtSecretKey(@Value("${localmart.jwt.secret}") String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean   // makes tokens (used at login)
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean   // checks tokens on every request: signature + expiry
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
