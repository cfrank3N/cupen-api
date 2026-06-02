package se.cupen.dto.creation;

import org.springframework.web.multipart.MultipartFile;

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
public class CreatePlayer {

  private String name;
  private String city;
  private Integer pricemoney;
  private Integer rating;
  private MultipartFile image;

}
