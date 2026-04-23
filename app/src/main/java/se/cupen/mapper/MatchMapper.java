package se.cupen.mapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import se.cupen.dto.MatchDTO;
import se.cupen.dto.PlayerSpecificMatchDTO;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.model.MatchEvent;
import se.cupen.persistence.model.Team;
import se.cupen.util.EventType;
import se.cupen.util.MatchResult;

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

        String score = goalsTeamA + " - " + goalsTeamB;

        return MatchDTO.builder()
                .id(match.getId().toString())
                .playedAt(match.getPlayedAt())
                .tournamentYear(match.getTournament().getYear())
                .teamA(TeamMapper.toDTO(match.getTeamA()))
                .teamB(TeamMapper.toDTO(match.getTeamB()))
                .events(match.getEvents().stream().map(event -> MatchEventMapper.toDTO(event)).toList())
                .matchType(match.getMatchType())
                .group(match.getMatchGroup())
                .score(score)
                .build();
    }

    public static PlayerSpecificMatchDTO toPlayerSpecificDTO(Match match, List<Team> playerTeams) {

        UUID teamAId = match.getTeamA().getId();
        UUID teamBId = match.getTeamB().getId();

        long goalsTeamA = match.getEvents().stream().filter(
                event -> event.getTeam().getId().equals(teamAId))
                .count();
        long goalsTeamB = match.getEvents().stream().filter(
                event -> event.getTeam().getId().equals(teamBId))
                .count();

        String score = goalsTeamA + " - " + goalsTeamB;

        Team winner = deriveWinner(match);

        MatchResult result = winner == null ? MatchResult.DRAW
                : playerTeams.contains(winner) ? MatchResult.WIN : MatchResult.LOSS;

        return PlayerSpecificMatchDTO.builder()
                .id(match.getId().toString())
                .playedAt(match.getPlayedAt())
                .tournamentYear(match.getTournament().getYear())
                .teamA(TeamMapper.toDTO(match.getTeamA()))
                .teamB(TeamMapper.toDTO(match.getTeamB()))
                .events(match.getEvents().stream().map(event -> MatchEventMapper.toDTO(event)).toList())
                .matchType(match.getMatchType())
                .group(match.getMatchGroup())
                .score(score)
                .result(result)
                .build();
    }

    private static Team deriveWinner(Match match) {
        Map<Team, Long> goalsByTeam = match.getEvents().stream()
                .filter(event -> event.getType() == EventType.GOAL)
                .collect(Collectors.groupingBy(MatchEvent::getTeam, Collectors.counting()));

        long goalsA = goalsByTeam.getOrDefault(match.getTeamA(), 0L);
        long goalsB = goalsByTeam.getOrDefault(match.getTeamB(), 0L);

        if (goalsA == goalsB) {
            return null;
        }

        return goalsA > goalsB ? match.getTeamA() : match.getTeamB();
    }
}
