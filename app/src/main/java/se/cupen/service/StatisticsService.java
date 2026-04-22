package se.cupen.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.cupen.dto.MatchDTO;
import se.cupen.exception.ValidationException;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;
import se.cupen.persistence.repository.PlayerRepo;

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

  public List<MatchDTO> findAllMatchesPlayedByPlayer(String playerId) {

    // TODO: Finish this after creating responseobject and exceptionhandler
    throw new ValidationException("Not yet implemented");

  }
}
