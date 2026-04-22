package se.cupen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDTO {

  private String id;
  private String name;
  private String city;
  private long pricemoney;
  private Integer rating;

}
