package se.cupen.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseResponse {

  private String message;
  private boolean success;

  @Builder.Default
  private List<String> errors = new ArrayList<>();

}
