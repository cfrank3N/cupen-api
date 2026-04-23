package se.cupen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.cupen.dto.HeadToHeadPlayerStats;
import se.cupen.dto.PlayerSpecificMatchDTO;
import se.cupen.dto.PlayerSpecificTeamDTO;
import se.cupen.dto.SimplePlayerStatsDTO;
import se.cupen.service.StatisticsService;
import se.cupen.util.ResponseData;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  // TODO: Add endpoint /player/{id} that summarizes most of the stats

  @GetMapping("/player/{id}/matches")
  public ResponseEntity<ResponseData<List<PlayerSpecificMatchDTO>>> fetchAllPlayersMatches(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findAllMatchesPlayedByPlayer(id));
  }

  @GetMapping("/player/{id}/matches/latest")
  public ResponseEntity<ResponseData<List<PlayerSpecificMatchDTO>>> fetchPlayersLatestPlayedMatches(
      @PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findLatestFivePlayedMatchesByPlayer(id));
  }

  @GetMapping("/player/{id}/goals")
  public ResponseEntity<ResponseData<Long>> fetchPlayersScoredGoals(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findPlayersScoredGoals(id));
  }

  @GetMapping("/player/{id}/teams")
  public ResponseEntity<ResponseData<List<PlayerSpecificTeamDTO>>> fetchAllPlayersFormerTeams(
      @PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findAllTeamsByPlayer(id));
  }

  @GetMapping("/player/{id}/simplestats")
  public ResponseEntity<ResponseData<SimplePlayerStatsDTO>> fetchSimplePlayerStats(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findCompressedStatsForPlayer(id));
  }

  @GetMapping("/player/{id}/biggestwin")
  public ResponseEntity<ResponseData<PlayerSpecificMatchDTO>> fetchBiggestWinByPlayer(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findBiggestWinByPlayer(id));
  }

  @GetMapping("/player/{id}/biggestloss")
  public ResponseEntity<ResponseData<PlayerSpecificMatchDTO>> fetchBiggestLossByPlayer(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findBiggestLossByPlayer(id));
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

}
