package se.cupen.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.cupen.exception.ValidationException;
import se.cupen.persistence.model.User;
import se.cupen.service.JwtService;
import se.cupen.service.UserService;
import se.cupen.util.ResponseData;

public class JwtFilter extends OncePerRequestFilter {
  private final String[] AUTHENTICATED_PATHS;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();
  private final JwtService jwtService;
  private final UserService userService;
  private final ObjectMapper objectMapper;
  private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

  public JwtFilter(String[] permittedPaths, JwtService jwtService, UserService userService,
      com.fasterxml.jackson.databind.ObjectMapper objectMapper,
      String[] authenticatedPaths) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.AUTHENTICATED_PATHS = authenticatedPaths;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String uri = request.getRequestURI();

    logger.info("Authenticated request at {} <-", uri);

    String authHeader = request.getHeader("Authorization");
    String username = null;
    String token = null;

    try {
      // First try jwt token to authorize all protected endpoints as normal
      if (authHeader != null && authHeader.startsWith("Bearer ")) {

        logger.info("Authenticating jwt token");

        token = authHeader.substring(7);
        username = jwtService.extractUsername(token);
      }

      // if jwt token is non present we check to see if the user wants to refresh an
      // expired jwt token
      // via /api/refresh. This is to keep the flow simple and let the controller do
      // as little as possible
      if (token == null && uri.equals("/api/refresh")) {

        logger.info("Authenticating refresh token");

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
          token = Arrays.stream(cookies)
              .filter(cookie -> cookie.getName().equals("refreshToken")).findFirst()
              .map(Cookie::getValue)
              .orElse(null);
        }
        if (token != null) {
          username = jwtService.extractUsername(token);
        }

      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        User user = userService.findByUsername(username);

        if (jwtService.validateToken(token, username)) {
          // find role from Jwt to insert into authToken
          SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Role.ADMIN);

          JwtPrincipal principal = JwtPrincipal.builder()
              .user(user)
              .orgIdLoggedInto(orgId)
              .build();

          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
              principal,
              null,
              List.of(authority));

          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      } else {
        logger.info("Validation of jwt failed at {}! No valid token present!", uri);
        writeCauseToClient("Invalid or missing token!", HttpServletResponse.SC_UNAUTHORIZED, response);
        return;
      }
    } catch (JwtException e) {
      logger.info("Validation of jwt failed! Cause: {}", e.getClass());
      writeCauseToClient("Invalid token", HttpServletResponse.SC_UNAUTHORIZED, response);
      return;
    } catch (ValidationException e) {
      logger.info("Validation of jwt failed! Cause: {}", e.getClass());
      writeCauseToClient("Invalid token! User not found", HttpServletResponse.SC_UNAUTHORIZED, response);
      return;
    }

    logger.info("validation of token successful for: {} at: {}", username, uri);
    logger.info("Authenticated request at {} ->", uri);

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    return Arrays.stream(AUTHENTICATED_PATHS)
        .noneMatch(pattern -> pathMatcher.match(pattern, path));
  }

  private void writeCauseToClient(String message,
      int responseCode,
      HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {

    ResponseData<String> responseData = ResponseData.failureWithStatusCode(message, responseCode);

    response.setStatus(responseCode);
    response.setContentType("application/json");
    objectMapper.writeValue(response.getWriter(), responseData);
  }
}
