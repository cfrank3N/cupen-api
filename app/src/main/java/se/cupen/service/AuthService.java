package se.cupen.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.cupen.exception.ValidationException;
import se.cupen.persistence.model.User;
import se.cupen.persistence.repository.UserRepo;
import se.cupen.util.ResponseData;

@Service
public class AuthService {
  private final UserRepo userRepo;
  private final JwtService jwtService;
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

  public AuthService(UserRepo userRepo, JwtService jwtService) {
    this.userRepo = userRepo;
    this.jwtService = jwtService;
  }

  @Transactional(readOnly = true)
  public ResponseData<TokenPair> login(UserLogin credentials) {

    logger.info("{} trying to log in!", credentials.getEmail());

    User user = findUserAndValidatePassword(credentials);

    // 3 minutes
    String jwtToken = jwtService.generateToken(user, jwtExpirationDate());
    // 7 hours
    String refreshTokenAsString = jwtService.generateToken(user, refreshTokenExpirationDate());

    ResponseCookie refreshToken = ResponseCookie
        .from("refreshToken", refreshTokenAsString)
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .path("/api/refresh")
        .maxAge(604800L) // 7 days
        .build();

    TokenPair tokenPair = TokenPair.builder()
        .jwtToken(jwtToken)
        .refreshToken(refreshToken)
        .build();

    logger.info("{} logged in!", credentials.getEmail());
    return ResponseData.successful(tokenPair, "Tokens generated");
  }

  /**
   * @param user
   * @param orgId
   * @return ResponseData<String>
   */
  @Transactional(readOnly = true)
  public ResponseData<String> refreshJwt() {

    // Get user and org from SecurityContextHolder to be able to send it forward to
    // create a new JWT
    String principal = SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal().toString();

    User user = findUserByUsername(principal);

    // generates jwt token for user to send back to client
    String jwtToken = jwtService.generateToken(user, jwtExpirationDate());

    return ResponseData.successful(jwtToken, "Tokens generated");
  }

  private User findUserAndValidatePassword(UserLogin credentials) throws ValidationException {

    // Find user
    User user = userRepo.findByEmail(credentials.getEmail())
        .orElseThrow(() -> new ValidationException(LOGIN_FAILURE, HttpStatus.UNAUTHORIZED.value()));

    // Check if passwords don't match to see if the client typed in the correct one
    if (!BCrypt.checkpw(credentials.getPassword(), user.getPassword())) {
      throw new ValidationException(LOGIN_FAILURE, HttpStatus.UNAUTHORIZED.value());
    }

    return user;

  }

  private User findUserByUsername(String username) {
    return userRepo.findByUsername(username)
        .orElseThrow(() -> new ValidationException("User not found", HttpStatus.NOT_FOUND.value()));
  }

  private Date jwtExpirationDate() {
    return new Date(System.currentTimeMillis() + 1000 * 60 * 3);
  }

  private Date refreshTokenExpirationDate() {
    return new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 7);
  }
}
