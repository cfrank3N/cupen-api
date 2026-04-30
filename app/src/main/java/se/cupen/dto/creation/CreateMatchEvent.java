package se.cupen.dto.creation;

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
public class CreateMatchEvent {
  private String playerId;
  private String matchId;
  private String teamId;
  private EventType eventType;
}
