package se.cupen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.cupen.util.EventType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchEventDTO {

  private String id;
  private PlayerDTO player;
  private EventType type;

}
