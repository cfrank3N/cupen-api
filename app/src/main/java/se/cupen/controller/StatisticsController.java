package se.cupen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import se.cupen.service.StatisticsService;
import se.cupen.util.ResponseData;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/players")
  public ResponseEntity<ResponseData<List<PlayerDTO>>> fetchAllPlayers() {
    return ResponseEntity.ok(statisticsService.allPlayers());
  }

  @GetMapping("/teams")
  public ResponseEntity<ResponseData<List<TeamDTO>>> fetchAllTeams() {
    return ResponseEntity.ok(statisticsService.allTeams());
  }

  @GetMapping("/matches")
  public ResponseEntity<ResponseData<List<MatchDTO>>> fetchAllMatches() {
    return ResponseEntity.ok(statisticsService.allMatches());
  }

  @GetMapping("/tournaments")
  public ResponseEntity<ResponseData<List<TournamentDTO>>> fetchAllTournaments() {
    return ResponseEntity.ok(statisticsService.allTournaments());
  }
  // TODO: Add endpoint /player/{id} that summarizes most of the stats

  @GetMapping("player/{id}")
  public ResponseEntity<ResponseData<PlayerViewStats>> fetchPlayerStats(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.playerStats(id));
  }

  @GetMapping("/player/{id}/matches")
  public ResponseEntity<ResponseData<List<PlayerSpecificMatchDTO>>> fetchAllPlayersMatches(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.matchesPlayedByPlayer(id));
  }

  @GetMapping("/player/{id}/matches/latest")
  public ResponseEntity<ResponseData<List<PlayerSpecificMatchDTO>>> fetchPlayersLatestPlayedMatches(
      @PathVariable String id) {
    return ResponseEntity.ok(statisticsService.latestFivePlayedMatchesByPlayer(id));
  }

  @GetMapping("/player/{id}/goals")
  public ResponseEntity<ResponseData<Long>> fetchPlayersScoredGoals(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.playersScoredGoals(id));
  }

  @GetMapping("/player/{id}/teams")
  public ResponseEntity<ResponseData<List<PlayerSpecificTeamDTO>>> fetchAllPlayersFormerTeams(
      @PathVariable String id) {
    return ResponseEntity.ok(statisticsService.allTeamsByPlayer(id));
  }

  @GetMapping("/player/{id}/simplestats")
  public ResponseEntity<ResponseData<SimplePlayerStatsDTO>> fetchSimplePlayerStats(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.compressedStatsForPlayer(id));
  }

  @GetMapping("/player/{id}/biggestwin")
  public ResponseEntity<ResponseData<PlayerSpecificMatchDTO>> fetchBiggestWinByPlayer(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.biggestWinByPlayer(id));
  }

  @GetMapping("/player/{id}/biggestloss")
  public ResponseEntity<ResponseData<PlayerSpecificMatchDTO>> fetchBiggestLossByPlayer(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.biggestLossByPlayer(id));
  }

  @GetMapping("/player/{id}/versus/{idTwo}")
  public ResponseEntity<ResponseData<List<PlayerSpecificMatchDTO>>> fetchPlayersHeadToHeadMatches(
      @PathVariable String id, @PathVariable String idTwo) {
    return ResponseEntity.ok(statisticsService.findHeadToHeadRecords(id, idTwo));
  }

  @GetMapping("/player/{id}/headtohead")
  public ResponseEntity<ResponseData<List<HeadToHeadPlayerStats>>> fetchStatsAgainstAllPlayers(
      @PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findStatsAgainsAllPlayers(id));
  }

  @GetMapping("/goals")
  public ResponseEntity<ResponseData<List<GoalsScoredByPlayer>>> fetchAllPlayersScoredGoals() {
    return ResponseEntity.ok(statisticsService.goalsScoredByAllPlayersSorted());
  }

  @GetMapping("/marathontable")
  public ResponseEntity<ResponseData<List<PlayerStats>>> fetchAllPlayersTotalScore() {
    return ResponseEntity.ok(statisticsService.AllPlayersTotalScore());
  }

  @GetMapping("/teamgoals")
  public ResponseEntity<ResponseData<List<PlayerTeamGoalStats>>> fetchAllPlayersTeamGoalStats() {
    return ResponseEntity.ok(statisticsService.averageTeamGoalsScoredByAllPlayers());
  }

}
