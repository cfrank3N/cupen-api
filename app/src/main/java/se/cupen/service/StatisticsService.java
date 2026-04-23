package se.cupen.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import se.cupen.dto.PlayerSpecificMatchDTO;
import se.cupen.dto.PlayerSpecificTeamDTO;
import se.cupen.dto.SimplePlayerStatsDTO;
import se.cupen.exception.ValidationException;
import se.cupen.mapper.MatchMapper;
import se.cupen.mapper.PlayerMapper;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.model.MatchEvent;
import se.cupen.persistence.model.Player;
import se.cupen.persistence.model.Team;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;
import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.util.EventType;
import se.cupen.util.MatchResult;
import se.cupen.util.ResponseData;

@Service
public class StatisticsService {

  private final MatchRepo matchRepo;
  private final MatchEventRepo matchEventRepo;
  private final PlayerRepo playerRepo;

  public StatisticsService(MatchRepo matchRepo, MatchEventRepo matchEventRepo, PlayerRepo playerRepo) {
    this.matchEventRepo = matchEventRepo;
    this.matchRepo = matchRepo;
    this.playerRepo = playerRepo;
  }

  // TODO: Add non required RequestParams like year, matchType, etc.
  /**
   * @param playerId
   * @return
   */
  public ResponseData<List<PlayerSpecificMatchDTO>> findAllMatchesPlayedByPlayer(String playerId) {

    Player player = playerRepo.findById(validateIdAndTransformToUuid(playerId))
        .orElseThrow(() -> new ValidationException("Player not found", 404));

    List<Team> playersTeams = player.getTeams();

    List<PlayerSpecificMatchDTO> playersMatches = matchRepo.findAll().stream()
        .filter(match -> playersTeams.contains(match.getTeamA())
            || playersTeams.contains(match.getTeamB()))
        .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
        .map(match -> MatchMapper.toPlayerSpecificDTO(match, playersTeams)).toList();

    return ResponseData.successful(playersMatches, "Matches fetched");

  }

  /**
   * @param playerId
   * @return
   */
  public ResponseData<List<PlayerSpecificMatchDTO>> findLatestFivePlayedMatchesByPlayer(String playerId) {
    List<PlayerSpecificMatchDTO> fiveLatestMatches = findAllMatchesPlayedByPlayer(playerId).getObject().stream()
        .limit(5).toList();
    return ResponseData.successful(fiveLatestMatches, "5 Latest matches fetched");
  }

  /**
   * @param playerId
   * @return
   */
  public ResponseData<Long> findPlayersScoredGoals(String playerId) {

    Player player = findPlayerById(playerId);

    List<MatchEvent> playersGoals = findPlayersGoals(player.getId());

    Long scoredGoals = playersGoals.stream().filter(event -> event.getType().equals(EventType.GOAL)).count();

    return ResponseData.successful(scoredGoals, "Scored goals fetched");

  }

  /**
   * @param playerId
   * @return
   */
  public ResponseData<List<PlayerSpecificTeamDTO>> findAllTeamsByPlayer(String playerId) {

    Player player = findPlayerById(playerId);

    List<Team> teams = player.getTeams();

    List<Match> allMatches = matchRepo.findAll();

    List<PlayerSpecificTeamDTO> teamStats = teams.stream()
        .map(team -> buildTeamStats(team, allMatches))
        .toList();

    return ResponseData.successful(teamStats, "Team stats fetched");

  }

  public ResponseData<SimplePlayerStatsDTO> findCompressedStatsForPlayer(String playerId) {

    List<PlayerSpecificTeamDTO> playerTeamStats = findAllTeamsByPlayer(playerId).getObject();

    int playedMatches = playerTeamStats.stream()
        .mapToInt(stats -> stats.getLosses() + stats.getWins() + stats.getDraws()).sum();
    int scoredGoals = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getScoredGoals).sum();
    int concededGoals = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getConcededGoals).sum();
    int wonMatches = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getWins).sum();
    int drawnMatches = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getDraws).sum();
    int lostMatches = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getLosses).sum();

    // TODO: Fix titles
    SimplePlayerStatsDTO stats = SimplePlayerStatsDTO.builder()
        .playedMatches(playedMatches)
        .wonMatches(wonMatches)
        .drawnMatches(drawnMatches)
        .lostMatches(lostMatches)
        .goalDifference(scoredGoals + "-" + concededGoals)
        .build();

    return ResponseData.successful(stats, "Simple stats fetched");
  }

  public ResponseData<PlayerSpecificMatchDTO> findBiggestWinByPlayer(String playerId) {

    PlayerSpecificMatchDTO biggestWin = findAllMatchesPlayedByPlayer(playerId)
        .getObject()
        .stream()
        .filter(match -> match.getResult().equals(MatchResult.WIN))
        .max(Comparator.comparing(PlayerSpecificMatchDTO::getGoalDifference))
        .orElseThrow(() -> new ValidationException("Player has no wins yet", 204));

    return ResponseData.successful(biggestWin, "Biggest win fetched");

  }

  public ResponseData<PlayerSpecificMatchDTO> findBiggestLossByPlayer(String playerId) {

    PlayerSpecificMatchDTO biggestLoss = findAllMatchesPlayedByPlayer(playerId)
        .getObject()
        .stream()
        .filter(match -> match.getResult().equals(MatchResult.LOSS))
        .min(Comparator.comparing(PlayerSpecificMatchDTO::getGoalDifference))
        .orElseThrow(() -> new ValidationException("Player has no losses yet", 204));

    return ResponseData.successful(biggestLoss, "Biggest loss fetched");
  }

  public ResponseData<List<PlayerSpecificMatchDTO>> findHeadToHeadRecords(String playerOneId, String playerTwoId) {

    String playerTwosId = findPlayerById(playerTwoId).getId().toString();

    List<PlayerSpecificMatchDTO> headToHeadGames = findAllMatchesPlayedByPlayer(playerOneId).getObject()
        .stream()
        .filter(match -> match
            .getTeamA()
            .getPlayers()
            .stream()
            .anyMatch(player -> player.getId().equals(playerTwosId))
            || match
                .getTeamB()
                .getPlayers()
                .stream()
                .anyMatch(player -> player.getId().equals(playerTwosId)))
        .toList();

    return ResponseData.successful(headToHeadGames, "Head to head games fetched");

  }

  /**
   * @param team
   * @param matches
   * @return
   */
  private PlayerSpecificTeamDTO buildTeamStats(Team team, List<Match> matches) {

    List<Match> teamMatches = matches.stream()
        .filter(match -> match.getTeamA().equals(team) || match.getTeamB().equals(team)).toList();

    int wins = 0;
    int losses = 0;
    int draws = 0;
    int scoredGoals = 0;
    int concededGoals = 0;

    for (Match match : teamMatches) {

      UUID teamId = team.getId();
      UUID opponentId = match.getTeamA().equals(team)
          ? match.getTeamB().getId()
          : match.getTeamA().getId();

      long teamGoals = match.getEvents().stream()
          .filter(event -> event.getType().equals(EventType.GOAL) && event.getTeam().getId().equals(teamId))
          .count();

      long opponentGoals = match.getEvents().stream()
          .filter(event -> event.getType().equals(EventType.GOAL) && event.getTeam().getId().equals(opponentId))
          .count();

      scoredGoals += teamGoals;
      concededGoals += opponentGoals;

      if (teamGoals == opponentGoals) {
        draws++;
      } else if (teamGoals > opponentGoals) {
        wins++;
      } else {
        losses++;
      }

    }

    return PlayerSpecificTeamDTO.builder()
        .id(team.getId().toString())
        .players(team.getPlayers().stream().map(PlayerMapper::toDTO).toList())
        .tournamentYear(team.getTournament().getYear())
        .wins(wins)
        .losses(losses)
        .draws(draws)
        .scoredGoals(scoredGoals)
        .concededGoals(concededGoals)
        .build();

  }

  /**
   * @param playerId
   * @return
   */
  private Player findPlayerById(String playerId) {

    return playerRepo.findById(validateIdAndTransformToUuid(playerId))
        .orElseThrow(() -> new ValidationException("Player not found", 404));

  }

  /**
   * @param playerId
   * @return
   */
  private List<MatchEvent> findPlayersGoals(UUID playerId) {

    return matchEventRepo.findAllByPlayerId(playerId)
        .orElseThrow(() -> new ValidationException("Player has no events registered", 404));

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
}
