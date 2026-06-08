package se.cupen.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.cupen.persistence.model.Player;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatsAndTeams {
  private Player player;
  private SimplePlayerStatsDTO stats;
  private List<PlayerSpecificTeamDTO> teams;
}
