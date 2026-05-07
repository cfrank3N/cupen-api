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
public class PlayerViewStats {
  private String name;
  private List<PlayerSpecificMatchDTO> lastFiveMatches;
  private List<PlayerSpecificTeamDTO> formerTeams;
  private SimplePlayerStatsDTO stats;
}
