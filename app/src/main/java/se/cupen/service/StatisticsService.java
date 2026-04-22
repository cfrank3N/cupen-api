package se.cupen.service;

import org.springframework.stereotype.Service;

import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;

@Service
public class StatisticsService {

  private final MatchRepo matchRepo;
  private final MatchEventRepo matchEventRepo;

  public StatisticsService(MatchRepo matchRepo, MatchEventRepo matchEventRepo) {
    this.matchEventRepo = matchEventRepo;
    this.matchRepo = matchRepo;
  }
}
