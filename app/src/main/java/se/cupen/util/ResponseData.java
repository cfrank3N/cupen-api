package se.cupen.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class ResponseData<T> extends BaseResponse {

  private T object;
  private int status;

  public static <T> ResponseData<T> successful(T object, String message) {
    return ResponseData.<T>builder()
        .object(object)
        .message(message)
        .success(true)
        .build();
  }

  public static <T> ResponseData<T> failure(String message) {
    return ResponseData.<T>builder()
        .object(null)
        .message(message)
        .success(false)
        .build();
  }

  public static <T> ResponseData<T> failureWithStatusCode(String message, int statusCode) {
    return ResponseData.<T>builder()
        .object(null)
        .message(message)
        .success(false)
        .status(statusCode)
        .build();
  }

}
