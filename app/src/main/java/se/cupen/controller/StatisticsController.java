package se.cupen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.cupen.dto.PlayerSpecificMatchDTO;
import se.cupen.service.StatisticsService;
import se.cupen.util.ResponseData;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/player/{id}/matches")
  public ResponseEntity<ResponseData<List<PlayerSpecificMatchDTO>>> fetchAllPlayersMatches(@PathVariable String id) {
    return ResponseEntity.ok(statisticsService.findAllMatchesPlayedByPlayer(id));
  }

}
