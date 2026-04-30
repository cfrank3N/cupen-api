package se.cupen.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import se.cupen.dto.MatchDTO;
import se.cupen.dto.MatchEventDTO;
import se.cupen.dto.creation.CreateMatch;
import se.cupen.dto.creation.CreateMatchEvent;
import se.cupen.exception.ValidationException;
import se.cupen.mapper.MatchEventMapper;
import se.cupen.mapper.MatchMapper;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.model.MatchEvent;
import se.cupen.persistence.model.Player;
import se.cupen.persistence.model.Team;
import se.cupen.persistence.model.Tournament;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;
import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.persistence.repository.TeamRepo;
import se.cupen.util.EventType;
import se.cupen.util.MatchGroup;
import se.cupen.util.MatchType;
import se.cupen.util.ResponseData;

@Service
public class MatchService {

  private final MatchRepo matchRepo;
  private final MatchEventRepo matchEventRepo;
  private final PlayerRepo playerRepo;
  private final TeamRepo teamRepo;

  public MatchService(MatchRepo matchRepo, MatchEventRepo matchEventRepo, TeamRepo teamRepo, PlayerRepo playerRepo) {
    this.matchEventRepo = matchEventRepo;
    this.matchRepo = matchRepo;
    this.teamRepo = teamRepo;
    this.playerRepo = playerRepo;
  }

  public List<MatchDTO> findAllMatches() {
    return matchRepo.findAll().stream()
        .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
        .map(match -> MatchMapper.toDTO(match)).toList();
  }

  public ResponseData<List<MatchEventDTO>> createMatchEvents(List<CreateMatchEvent> events) {

    Set<UUID> playerIds = events.stream()
        .map(event -> validateIdAndTransformToUuid(event.getPlayerId()))
        .collect(Collectors.toSet());

    Set<UUID> teamIds = events.stream()
        .map(event -> validateIdAndTransformToUuid(event.getTeamId()))
        .collect(Collectors.toSet());

    Set<UUID> matchIds = events.stream()
        .map(event -> validateIdAndTransformToUuid(event.getMatchId()))
        .collect(Collectors.toSet());

    Map<UUID, Player> players = playerRepo.findAllById(playerIds).stream()
        .collect(Collectors.toMap(Player::getId, player -> player));

    Map<UUID, Team> teams = teamRepo.findAllById(teamIds).stream()
        .collect(Collectors.toMap(Team::getId, team -> team));

    Map<UUID, Match> matches = matchRepo.findAllById(matchIds).stream()
        .collect(Collectors.toMap(Match::getId, match -> match));

    List<MatchEvent> eventsToSave = events.stream()
        .map(event -> {
          Player player = Optional.ofNullable(players.get(validateIdAndTransformToUuid(event.getPlayerId())))
              .orElseThrow(() -> new ValidationException("Player not found", 404));

          Team team = Optional.ofNullable(teams.get(validateIdAndTransformToUuid(event.getTeamId())))
              .orElseThrow(() -> new ValidationException("Team not found", 404));

          Match match = Optional.ofNullable(matches.get(validateIdAndTransformToUuid(event.getMatchId())))
              .orElseThrow(() -> new ValidationException("Match not found", 404));

          if (!match.getTeamA().equals(team) && !match.getTeamB().equals(team)) {
            throw new ValidationException("Team is not part of this match", 400);
          }
          if (!team.getPlayers().contains(player)) {
            throw new ValidationException(player.getName() + " is not part of this team", 400);
          }

          return MatchEvent.builder()
              .player(player)
              .match(match)
              .team(team)
              .type(event.getEventType())
              .build();
        }).toList();

    List<MatchEventDTO> savedEvents = matchEventRepo.saveAll(eventsToSave).stream()
        .map(MatchEventMapper::toDTO)
        .toList();

    return ResponseData.successful(savedEvents, "Events saved");

  }

  public ResponseData<List<MatchDTO>> createMatches(List<CreateMatch> matches) {

    validateTeamsAreUniqueInMatches(matches);

    Map<UUID, Team> teams = findAllTeamsAndTransformToMap(matches);

    List<Match> matchesToSave = matches.stream()
        .map(match -> {

          UUID teamAId = validateIdAndTransformToUuid(match.getTeamAId());
          UUID teamBId = validateIdAndTransformToUuid(match.getTeamBId());

          Team teamA = teams.get(teamAId);
          Team teamB = teams.get(teamBId);

          Match m = Match.builder()
              .teamA(teamA)
              .teamB(teamB)
              .playedAt(match.getPlayedAt())
              .matchGroup(match.getMatchGroup())
              .matchType(match.getMatchType())
              .build();

          m.setTournament(validateTeamsExistsInTheSameTournament(m));

          return m;
        })
        .toList();

    List<MatchDTO> savedMatches = matchRepo.saveAll(matchesToSave).stream().map(MatchMapper::toDTO).toList();

    return ResponseData.successful(savedMatches, "Matches saved");
  }

  public ResponseData<MatchType[]> findAllMatchTypes() {
    return ResponseData.successful(MatchType.values(), "MatchTypes fetched");
  }

  public ResponseData<EventType[]> findAllEventTypes() {
    return ResponseData.successful(EventType.values(), "EventTypes fetched");
  }

  public ResponseData<MatchGroup[]> findAllMatchGroups() {
    return ResponseData.successful(MatchGroup.values(), "MatchGroups fetched");
  }

  /**
   * @param matches
   * @return
   */
  private Map<UUID, Team> findAllTeamsAndTransformToMap(List<CreateMatch> matches) {
    Set<UUID> teamIds = matches.stream()
        .flatMap(match -> Stream.of(match.getTeamAId(), match.getTeamBId()))
        .map(this::validateIdAndTransformToUuid)
        .collect(Collectors.toSet());

    Map<UUID, Team> teams = teamRepo.findAllById(teamIds).stream()
        .collect(Collectors.toMap(Team::getId, Function.identity()));

    if (teamIds.size() != teams.size()) {
      throw new ValidationException("One or more teams not found", 404);
    }

    return teams;
  }

  /**
   * @param match
   * @return
   */
  private Tournament validateTeamsExistsInTheSameTournament(Match match) {

    Tournament tournamentOne = match.getTeamA().getTournament();
    Tournament tournamentTwo = match.getTeamB().getTournament();

    if (!tournamentOne.getId().equals(tournamentTwo.getId())) {
      throw new ValidationException("Teams must exist in the same tournament", 400);
    }

    return tournamentOne;
  }

  /**
   * @param id
   * @return
   */
  private UUID validateIdAndTransformToUuid(String id) {
    try {
      if (id.length() != 36) {
        throw new ValidationException("Id must be of type UUID v4", 400);
      }
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new ValidationException("Id must be of type UUID v4", 400);
    }
  }

  /**
   * @param matches
   * @return
   */
  private boolean validateTeamsAreUniqueInMatches(List<CreateMatch> matches) {
    boolean sameTeams = matches.stream().anyMatch(match -> match.getTeamAId().equals(match.getTeamBId()));

    if (sameTeams) {
      throw new ValidationException("A match must consist of two different teams", 400);
    }

    return true;
  }
}
