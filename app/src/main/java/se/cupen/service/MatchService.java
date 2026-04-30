package se.cupen.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import se.cupen.dto.MatchDTO;
import se.cupen.dto.creation.CreateMatch;
import se.cupen.exception.ValidationException;
import se.cupen.mapper.MatchMapper;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.model.Team;
import se.cupen.persistence.model.Tournament;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;
import se.cupen.persistence.repository.TeamRepo;
import se.cupen.util.ResponseData;

@Service
public class MatchService {

  private final MatchRepo matchRepo;
  private final MatchEventRepo matchEventRepo;
  private final TeamRepo teamRepo;

  public MatchService(MatchRepo matchRepo, MatchEventRepo matchEventRepo, TeamRepo teamRepo) {
    this.matchEventRepo = matchEventRepo;
    this.matchRepo = matchRepo;
    this.teamRepo = teamRepo;
  }

  public List<MatchDTO> findAllMatches() {
    return matchRepo.findAll().stream()
        .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
        .map(match -> MatchMapper.toDTO(match)).toList();
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
   * @param playerId
   * @return
   */
  private Team findTeamById(String teamId) {

    return teamRepo.findById(validateIdAndTransformToUuid(teamId))
        .orElseThrow(() -> new ValidationException("Team not found", 404));

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
  // TODO: Create method to update matches with match events.
  // Should be able to do this for many games at a time
}
