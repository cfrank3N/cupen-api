package se.cupen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.cupen.filter.JwtFilter;
import se.cupen.service.JwtService;
import se.cupen.service.UserService;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

  private final String[] AUTHENTICATED_PATHS = { "/auth/refresh", "/api/players", "/api/teams/{tournamentId}",
      "/api/matches", "/api/matches/events", "/api/tournaments" };

  private final JwtService jwtService;
  private final UserService userService;
  private final ObjectMapper objectMapper;
  private final CorsConfigurationSource corsConfigurationSource;

  public SecurityConfig(JwtService jwtService, UserService userService, ObjectMapper objectMapper,
      CorsConfigurationSource corsConfigurationSource) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.objectMapper = objectMapper;
    this.corsConfigurationSource = corsConfigurationSource;
  }

  @Bean
  public JwtFilter jwtFilter() {
    return new JwtFilter(jwtService, userService, objectMapper, AUTHENTICATED_PATHS);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(org.springframework.http.HttpMethod.GET, AUTHENTICATED_PATHS).permitAll()
            .requestMatchers(AUTHENTICATED_PATHS).authenticated()
            .anyRequest().permitAll())
        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
