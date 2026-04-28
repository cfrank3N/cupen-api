package se.cupen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.cupen.filter.JwtFilter;
import se.cupen.service.JwtService;
import se.cupen.service.UserService;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

  private final String[] AUTHENTICATED_PATHS = { "/auth/refresh" };

  private final JwtService jwtService;
  private final UserService userService;
  private final ObjectMapper objectMapper;

  public SecurityConfig(JwtService jwtService, UserService userService, ObjectMapper objectMapper) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.objectMapper = objectMapper;
  }

  @Bean
  public JwtFilter jwtFilter() {
    return new JwtFilter(AUTHENTICATED_PATHS, jwtService, userService, objectMapper, AUTHENTICATED_PATHS);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(AUTHENTICATED_PATHS).authenticated()
            .anyRequest().permitAll())
        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
