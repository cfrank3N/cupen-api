package se.cupen.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.cupen.dto.TokenPair;
import se.cupen.dto.UserLogin;
import se.cupen.service.AuthService;
import se.cupen.util.ResponseData;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseData<String>> login(@RequestBody UserLogin credentials) {

    logger.info("Logging in <-");

    TokenPair tokens = authService.login(credentials).getObject();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, tokens.getRefreshToken().toString());

    ResponseData<String> response = ResponseData.successful(tokens.getJwtToken(), "Tokens generated");

    logger.info("Logging in ->");

    return ResponseEntity.ok().headers(headers).body(response);
  }

  @GetMapping("/refresh")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<ResponseData<String>> refreshJwt() {

    logger.info("Refreshing token <-");

    ResponseData<String> response = authService.refreshJwt();

    logger.info("Refreshing token ->");

    return ResponseEntity.ok(response);

  }
}
