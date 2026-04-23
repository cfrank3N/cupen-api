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
public class HeadToHeadPlayerStats {
  private String playerName;
  private int playedMatches;
  // TODO: Implement more fields
}
