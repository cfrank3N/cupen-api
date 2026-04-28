package se.cupen.filter;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.cupen.service.JwtService;
import se.cupen.util.ResponseData;

public class JwtFilter extends OncePerRequestFilter {
  private final String[] PERMITTED_PATHS;
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
    this.PERMITTED_PATHS = permittedPaths;
    this.AUTHENTICATED_PATHS = authenticatedPaths;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

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
