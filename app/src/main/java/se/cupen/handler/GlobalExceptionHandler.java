package se.cupen.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import se.cupen.exception.ValidationException;
import se.cupen.util.ResponseData;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ResponseData<?>> handleValidationException(HttpServletRequest request, ValidationException e) {
    int status = e.getStatusCode();
    String message = e.getMessage();
    String uri = request.getRequestURI();
    ResponseData<?> response = ResponseData.failureWithStatusCode(message, status);
    response.setErrors(List.of("VALIDATION"));

    log.info("{}:{} at {}", status, message, uri);

    return ResponseEntity.status(e.getStatusCode())
        .body(response);
  }
}
