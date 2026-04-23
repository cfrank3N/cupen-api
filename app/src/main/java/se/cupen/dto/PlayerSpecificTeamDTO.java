package se.cupen.dto;

import java.util.List;

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
public class PlayerSpecificTeamDTO {
  private String id;
  private List<PlayerDTO> players;
  private Integer tournamentYear;
  private Integer wins;
  private Integer losses;
  private Integer draws;
  private Integer scoredGoals;
  private Integer concededGoals;
  private Integer placement;
}
