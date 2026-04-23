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
public class SimplePlayerStatsDTO {

  private Integer playedMatches;
  private Integer wonMatches;
  private Integer drawnMatches;
  private Integer lostMatches;
  private String goalDifference;
  private Integer titles;

}
