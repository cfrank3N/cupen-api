package se.cupen.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.cupen.service.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

}
