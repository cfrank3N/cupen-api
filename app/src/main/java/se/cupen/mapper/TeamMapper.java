package se.cupen.mapper;

import se.cupen.dto.PlayerSpecificTeamDTO;
import se.cupen.dto.TeamDTO;
import se.cupen.persistence.model.Team;

public class TeamMapper {

  public static TeamDTO toDTO(Team team) {
    return TeamDTO.builder()
        .id(team.getId().toString())
        .players(team.getPlayers().stream().map(player -> PlayerMapper.toDTO(player)).toList())
        .build();
  }

  public static PlayerSpecificTeamDTO toPlayerSpecificDTO(Team team) {
    return PlayerSpecificTeamDTO.builder()
        .id(team.getId().toString())
        .players(team.getPlayers().stream().map(player -> PlayerMapper.toDTO(player)).toList())
        .tournamentYear(team.getTournament().getYear())
        .build();
  }
}
