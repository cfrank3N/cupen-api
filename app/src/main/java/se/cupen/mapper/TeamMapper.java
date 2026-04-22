package se.cupen.mapper;

import se.cupen.dto.TeamDTO;
import se.cupen.persistence.model.Team;

public class TeamMapper {

  public static TeamDTO toDTO(Team team) {
    return TeamDTO.builder()
        .id(team.getId().toString())
        .players(team.getPlayers().stream().map(player -> PlayerMapper.toDTO(player)).toList())
        .build();
  }
}
