package se.cupen.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import se.cupen.dto.GoalsScoredByPlayer;
import se.cupen.dto.HeadToHeadPlayerStats;
import se.cupen.dto.MatchDTO;
import se.cupen.dto.PlayerDTO;
import se.cupen.dto.PlayerStats;
import se.cupen.dto.PlayerTeamGoalStats;
import se.cupen.dto.PlayerSpecificMatchDTO;
import se.cupen.dto.PlayerSpecificTeamDTO;
import se.cupen.dto.PlayerViewStats;
import se.cupen.dto.SimplePlayerStatsDTO;
import se.cupen.dto.TeamDTO;
import se.cupen.dto.TournamentDTO;
import se.cupen.exception.ValidationException;
import se.cupen.mapper.MatchMapper;
import se.cupen.mapper.PlayerMapper;
import se.cupen.mapper.TeamMapper;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.model.MatchEvent;
import se.cupen.persistence.model.Player;
import se.cupen.persistence.model.Team;
import se.cupen.persistence.model.Tournament;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;
import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.persistence.repository.TeamRepo;
import se.cupen.persistence.repository.TournamentRepo;
import se.cupen.util.EventType;
import se.cupen.util.MatchResult;
import se.cupen.util.MatchType;
import se.cupen.util.ResponseData;

@Service
public class StatisticsService {

    private final MatchRepo matchRepo;
    private final MatchEventRepo matchEventRepo;
    private final PlayerRepo playerRepo;
    private final TournamentRepo tournamentRepo;
    private final TeamRepo teamRepo;

    public StatisticsService(MatchRepo matchRepo, MatchEventRepo matchEventRepo, PlayerRepo playerRepo,
            TournamentRepo tournamentRepo, TeamRepo teamRepo) {
        this.matchEventRepo = matchEventRepo;
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
        this.tournamentRepo = tournamentRepo;
        this.teamRepo = teamRepo;
    }

    // TODO: Add get all players in this sercvice and controller and change the api
    // call in the frontend
    // TODO: Add non required RequestParams like year, matchType, etc.
    /**
     * @param playerId
     * @return
     */
    public ResponseData<List<PlayerSpecificMatchDTO>> matchesPlayedByPlayer(String playerId) {

        Player player = findPlayerById(playerId);
        return findAllMatchesPlayedByPlayer(player);

    }

    public ResponseData<List<TournamentDTO>> allTournaments() {
        List<TournamentDTO> tournaments = tournamentRepo.findAll().stream()
                .sorted(Comparator.comparing(Tournament::getYear))
                .map(t -> {
                    return TournamentDTO.builder()
                            .id(t.getId())
                            .year(t.getYear())
                            .teams(t.getTeams().stream().map(TeamMapper::toDTO).toList())
                            .build();
                })
                .toList();

        return ResponseData.successful(tournaments, "Tournaments fetched");
    }

    public ResponseData<List<PlayerDTO>> allPlayers() {
        List<PlayerDTO> players = playerRepo.findAll().stream().map(player -> PlayerMapper.toDTO(player)).toList();
        return ResponseData.successful(players, "All players fetched");
    }

    public ResponseData<List<MatchDTO>> allMatches() {
        List<MatchDTO> matches = matchRepo.findAll().stream()
                .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
                .map(match -> MatchMapper.toDTO(match)).toList();

        return ResponseData.successful(matches, "Matches fetched");
    }

    public ResponseData<List<TeamDTO>> allTeams() {
        List<TeamDTO> teams = teamRepo.findAll().stream().map(TeamMapper::toDTO).toList();
        return ResponseData.successful(teams, "All teams fetched");
    }

    public ResponseData<PlayerViewStats> playerStats(String playerId) {
        Player player = findPlayerById(playerId);
        List<PlayerSpecificMatchDTO> lastFiveMatches = findAllMatchesPlayedByPlayer(player).getObject().stream()
                .limit(5).toList();
        List<PlayerSpecificTeamDTO> teams = findAllTeamsByPlayer(player);
        SimplePlayerStatsDTO stats = findCompressedStatsForPlayer(player);
        List<HeadToHeadPlayerStats> statsAgainstAll = findStatsAgainsAllPlayers(player.getId().toString()).getObject();
        PlayerSpecificMatchDTO biggestWin = findBiggestWinByPlayer(player);
        PlayerSpecificMatchDTO biggestLoss = findBiggestLossByPlayer(player);
        Integer rating = calculatePlayerRating(player, stats, teams);

        PlayerViewStats playerStats = PlayerViewStats.builder()
                .name(player.getName())
                .imageUrl(player.getImageUrl())
                .rating(rating)
                .lastFiveMatches(lastFiveMatches)
                .formerTeams(teams)
                .stats(stats)
                .statsAgainstAll(statsAgainstAll)
                .biggestWin(biggestWin)
                .biggestLoss(biggestLoss)
                .build();

        return ResponseData.successful(playerStats, "Player stats fetched");

    }

    private ResponseData<List<PlayerSpecificMatchDTO>> findAllMatchesPlayedByPlayer(Player player) {
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
    public ResponseData<List<PlayerSpecificMatchDTO>> latestFivePlayedMatchesByPlayer(String playerId) {
        Player player = findPlayerById(playerId);
        List<PlayerSpecificMatchDTO> fiveLatestMatches = findAllMatchesPlayedByPlayer(player).getObject().stream()
                .limit(5).toList();
        return ResponseData.successful(fiveLatestMatches, "5 Latest matches fetched");
    }

    /**
     * @param playerId
     * @return
     */
    public ResponseData<Long> playersScoredGoals(String playerId) {

        Player player = findPlayerById(playerId);

        Long scoredGoals = findPlayersScoredGoals(player);

        return ResponseData.successful(scoredGoals, "Scored goals fetched");

    }

    // TODO: calculate rating of player
    public Integer calculatePlayerRating(Player player, SimplePlayerStatsDTO playerStats,
            List<PlayerSpecificTeamDTO> teams) {

        int scoredTeamGoals = teams.stream().mapToInt(PlayerSpecificTeamDTO::getScoredGoals).sum();
        int concededTeamGoals = teams.stream().mapToInt(PlayerSpecificTeamDTO::getConcededGoals).sum();
        Long playerScoredGoals = findPlayersScoredGoals(player);
        int playedMatches = playerStats.getPlayedMatches();

        Double goalContribution = scoredTeamGoals > 0 ? (double) playerScoredGoals / scoredTeamGoals : 0;
        Double winrate = playerStats.getPlayedMatches() > 0
                ? ((double) playerStats.getWonMatches() + playerStats.getDrawnMatches() * 0.5)
                        / playerStats.getPlayedMatches()
                : 0.0;
        Double goalDifference = playedMatches > 0
                ? (double) (scoredTeamGoals - concededTeamGoals) / playedMatches
                : 0;

        Double rating = 40 + (goalContribution * 40) + (winrate * 15) + Math.min(goalDifference, 5) * 1;
        return (int) Math.round(Math.max(0, Math.min(100, rating)));

    }

    private long findPlayersScoredGoals(Player player) {
        List<MatchEvent> playerEvents = findPlayersMatchEvents(player.getId());
        return playerEvents.stream().filter(event -> event.getType().equals(EventType.GOAL)).count();
    }

    /**
     * @param playerId
     * @return
     */
    public ResponseData<List<PlayerSpecificTeamDTO>> allTeamsByPlayer(String playerId) {

        Player player = findPlayerById(playerId);

        List<PlayerSpecificTeamDTO> teamStats = findAllTeamsByPlayer(player);

        return ResponseData.successful(teamStats, "Team stats fetched");

    }

    private List<PlayerSpecificTeamDTO> findAllTeamsByPlayer(Player player) {

        List<Team> teams = player.getTeams();

        List<Match> allMatches = matchRepo.findAll();

        List<PlayerSpecificTeamDTO> teamStats = teams.stream()
                .map(team -> buildTeamStats(team, allMatches))
                .toList();

        return teamStats;
    }

    /**
     * @param playerId
     * @return
     */
    public ResponseData<SimplePlayerStatsDTO> compressedStatsForPlayer(String playerId) {

        Player player = findPlayerById(playerId);

        SimplePlayerStatsDTO stats = findCompressedStatsForPlayer(player);

        return ResponseData.successful(stats, "Simple stats fetched");
    }

    private SimplePlayerStatsDTO findCompressedStatsForPlayer(Player player) {

        List<PlayerSpecificTeamDTO> playerTeamStats = findAllTeamsByPlayer(player);

        int playedMatches = playerTeamStats.stream()
                .mapToInt(stats -> stats.getLosses() + stats.getWins() + stats.getDraws()).sum();
        int scoredGoals = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getScoredGoals).sum();
        int concededGoals = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getConcededGoals).sum();
        int wonMatches = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getWins).sum();
        int drawnMatches = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getDraws).sum();
        int lostMatches = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getLosses).sum();
        int titles = playerTeamStats.stream().mapToInt(PlayerSpecificTeamDTO::getTitles).sum();

        return SimplePlayerStatsDTO.builder()
                .playedMatches(playedMatches)
                .wonMatches(wonMatches)
                .drawnMatches(drawnMatches)
                .lostMatches(lostMatches)
                .goalDifference(scoredGoals + "-" + concededGoals)
                .titles(titles)
                .build();

    }

    public ResponseData<List<PlayerTeamGoalStats>> averageTeamGoalsScoredByAllPlayers() {

        List<Player> players = playerRepo.findAll();

        List<PlayerTeamGoalStats> teamGoalStats = players.stream()
                .map(player -> {

                    List<PlayerSpecificTeamDTO> playerTeams = findAllTeamsByPlayer(player);

                    int goals = playerTeams.stream().mapToInt(PlayerSpecificTeamDTO::getScoredGoals).sum();
                    int playedMatches = playerTeams.stream()
                            .mapToInt(stats -> stats.getLosses() + stats.getWins() + stats.getDraws()).sum();
                    double averageGoals = (double) goals / playedMatches;

                    return PlayerTeamGoalStats.builder()
                            .player(PlayerMapper.toDTO(player))
                            .goals(goals)
                            .averageGoals(averageGoals)
                            .build();

                }).sorted(Comparator.comparing(PlayerTeamGoalStats::getGoals).reversed())
                .toList();

        return ResponseData.successful(teamGoalStats, "Stats fetched");

    }

    public ResponseData<List<PlayerStats>> AllPlayersTotalScore() {
        List<Player> players = playerRepo.findAll();

        List<PlayerStats> stats = players.stream()
                .map(player -> {
                    SimplePlayerStatsDTO simpleStats = findCompressedStatsForPlayer(player);
                    int points = (simpleStats.getWonMatches() * 3) + (simpleStats.getDrawnMatches() * 2);

                    return PlayerStats.builder()
                            .player(PlayerMapper.toDTO(player))
                            .points(points)
                            .playedMatches(simpleStats.getPlayedMatches())
                            .wonMatches(simpleStats.getWonMatches())
                            .drawnMatches(simpleStats.getDrawnMatches())
                            .lostMatches(simpleStats.getLostMatches())
                            .goalDifference(simpleStats.getGoalDifference())
                            .titles(simpleStats.getTitles())
                            .build();
                })
                .sorted(Comparator.comparing(PlayerStats::getPoints).reversed())
                .toList();

        return ResponseData.successful(stats, "Player stats fetched");
    }

    /**
     * @param playerId
     * @return
     */
    public ResponseData<PlayerSpecificMatchDTO> biggestWinByPlayer(String playerId) {

        PlayerSpecificMatchDTO biggestWin = matchesPlayedByPlayer(playerId)
                .getObject()
                .stream()
                .filter(match -> match.getResult().equals(MatchResult.WIN))
                .max(Comparator.comparing(PlayerSpecificMatchDTO::getGoalDifference))
                .orElseThrow(() -> new ValidationException("Player has no wins yet", 204));

        return ResponseData.successful(biggestWin, "Biggest win fetched");
    }

    public PlayerSpecificMatchDTO findBiggestWinByPlayer(Player player) {
        PlayerSpecificMatchDTO biggestWin = findAllMatchesPlayedByPlayer(player).getObject().stream()
                .filter(match -> match.getResult().equals(MatchResult.WIN))
                .max(Comparator.comparing(PlayerSpecificMatchDTO::getGoalDifference))
                .orElse(null);

        return biggestWin;
    }

    public PlayerSpecificMatchDTO findBiggestLossByPlayer(Player player) {
        PlayerSpecificMatchDTO biggestLoss = findAllMatchesPlayedByPlayer(player).getObject().stream()
                .filter(match -> match.getResult().equals(MatchResult.LOSS))
                .max(Comparator.comparing(PlayerSpecificMatchDTO::getGoalDifference))
                .orElse(null);

        return biggestLoss;
    }

    /**
     * @param playerId
     * @return
     */
    public ResponseData<PlayerSpecificMatchDTO> biggestLossByPlayer(String playerId) {

        PlayerSpecificMatchDTO biggestLoss = matchesPlayedByPlayer(playerId)
                .getObject()
                .stream()
                .filter(match -> match.getResult().equals(MatchResult.LOSS))
                .min(Comparator.comparing(PlayerSpecificMatchDTO::getGoalDifference))
                .orElseThrow(() -> new ValidationException("Player has no losses yet", 204));

        return ResponseData.successful(biggestLoss, "Biggest loss fetched");
    }

    /**
     * @param playerOneId
     * @param playerTwoId
     * @return
     */
    public ResponseData<List<PlayerSpecificMatchDTO>> findHeadToHeadRecords(String playerOneId, String playerTwoId) {

        if (playerOneId.equals(playerTwoId)) {
            throw new ValidationException("Players can't be the same player", 400);
        }

        // Validate playerTwoId is valid or else throw exception
        findPlayerById(playerTwoId).getId().toString();

        List<PlayerSpecificMatchDTO> headToHeadMatches = matchesPlayedByPlayer(playerOneId).getObject()
                .stream()
                .filter(match -> {
                    boolean playerOneIsTeamA = match.getTeamA().getPlayers().stream()
                            .anyMatch(player -> player.getId().equals(playerOneId));

                    List<PlayerDTO> opposingTeamPlayers = playerOneIsTeamA
                            ? match.getTeamB().getPlayers()
                            : match.getTeamA().getPlayers();

                    return opposingTeamPlayers.stream()
                            .anyMatch(player -> player.getId().equals(playerTwoId));
                })
                .toList();

        return ResponseData.successful(headToHeadMatches, "Head to head games fetched");

    }

    public ResponseData<List<HeadToHeadPlayerStats>> findStatsAgainsAllPlayers(String playerId) {

        findPlayerById(playerId);

        List<Player> allPlayers = playerRepo.findAll().stream()
                .filter(player -> !player.getId().toString().equals(playerId))
                .toList();

        Map<UUID, List<PlayerSpecificMatchDTO>> allStats = allPlayers.stream()
                .collect(Collectors.toMap(
                        Player::getId,
                        player -> findHeadToHeadRecords(playerId, player.getId().toString()).getObject()))
                .entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<HeadToHeadPlayerStats> summarizedStats = allStats.entrySet().stream()
                .map(entry -> summarizeStatsPerOpponent(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(HeadToHeadPlayerStats::getWinPercentage))
                .toList();

        return ResponseData.successful(summarizedStats, "Stats fetched");

    }

    public ResponseData<List<GoalsScoredByPlayer>> goalsScoredByAllPlayersSorted() {

        List<Player> players = playerRepo.findAll();
        Map<UUID, List<MatchEvent>> events = matchEventRepo.findAll().stream()
                .collect(Collectors.groupingBy(event -> event.getPlayer().getId()));

        List<GoalsScoredByPlayer> goalStats = players.stream()
                .map(player -> {
                    PlayerDTO playerDTO = PlayerMapper.toDTO(player);
                    List<MatchEvent> playersEvents = events.getOrDefault(player.getId(), List.of());

                    int goals = (int) playersEvents.stream().filter(event -> event.getType().equals(EventType.GOAL))
                            .count();

                    return new GoalsScoredByPlayer(playerDTO, goals);
                })
                .sorted(Comparator.comparingInt(GoalsScoredByPlayer::getGoals).reversed())
                .toList();

        return ResponseData.successful(goalStats, "Goals stats fetched");

    }

    private HeadToHeadPlayerStats summarizeStatsPerOpponent(UUID opponentId,
            List<PlayerSpecificMatchDTO> matchesAgainstOpponent) {

        int playedMatches = matchesAgainstOpponent.size();
        int wonMatches = (int) matchesAgainstOpponent.stream()
                .filter(match -> match.getResult().equals(MatchResult.WIN))
                .count();
        int goalDifference = matchesAgainstOpponent.stream()
                .mapToInt(PlayerSpecificMatchDTO::getGoalDifference)
                .sum();
        double winPercentageRaw = (double) wonMatches / playedMatches * 100;
        double winPercentage = BigDecimal.valueOf(winPercentageRaw)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return HeadToHeadPlayerStats.builder()
                .playedMatches(playedMatches)
                .playerName(findPlayerById(opponentId.toString()).getName())
                .wonMatches(wonMatches)
                .goalDifference(goalDifference)
                .winPercentage(winPercentage)
                .build();

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
        int titles = 0;

        for (Match match : teamMatches) {

            UUID teamId = team.getId();
            UUID opponentId = match.getTeamA().equals(team)
                    ? match.getTeamB().getId()
                    : match.getTeamA().getId();

            long teamGoals = match.getEvents().stream()
                    .filter(event -> event.getType().equals(EventType.GOAL) && event.getTeam().getId().equals(teamId))
                    .count();

            long opponentGoals = match.getEvents().stream()
                    .filter(event -> event.getType().equals(EventType.GOAL)
                            && event.getTeam().getId().equals(opponentId))
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

            if (teamGoals > opponentGoals && match.getMatchType().equals(MatchType.FINAL)) {
                titles++;
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
                .titles(titles)
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
    private List<MatchEvent> findPlayersMatchEvents(UUID playerId) {

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
