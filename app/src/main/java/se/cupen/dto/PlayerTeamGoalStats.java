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
public class PlayerTeamGoalStats {
  private PlayerDTO player;
  private Integer goals;
  private Double averageGoals;
}
