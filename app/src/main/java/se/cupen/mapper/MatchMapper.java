package se.cupen.mapper;

import java.util.UUID;

import se.cupen.dto.MatchDTO;
import se.cupen.persistence.model.Match;

public class MatchMapper {

  public static MatchDTO toDTO(Match match) {

    UUID teamAId = match.getTeamA().getId();
    UUID teamBId = match.getTeamB().getId();

    long goalsTeamA = match.getEvents().stream().filter(
        event -> event.getTeam().getId().equals(teamAId))
        .count();
    long goalsTeamB = match.getEvents().stream().filter(
        event -> event.getTeam().getId().equals(teamBId))
        .count();

    String result = goalsTeamA + " - " + goalsTeamB;

    return MatchDTO.builder()
        .id(match.getId().toString())
        .playedAt(match.getPlayedAt())
        .tournamentYear(match.getTournament().getYear())
        .teamA(TeamMapper.toDTO(match.getTeamA()))
        .teamB(TeamMapper.toDTO(match.getTeamB()))
        .events(match.getEvents().stream().map(event -> MatchEventMapper.toDTO(event)).toList())
        .matchType(match.getMatchType())
        .group(match.getMatchGroup())
        .result(result)
        .build();
  }
}
