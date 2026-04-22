package se.cupen.exception;

public class ValidationException extends RuntimeException {
  private int statusCode;

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

}
