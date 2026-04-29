package se.cupen.service;

import org.springframework.stereotype.Service;

import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.persistence.repository.TeamRepo;

@Service
public class TeamService {

  private final TeamRepo teamRepo;
  private final PlayerRepo playerRepo;

  public TeamService(TeamRepo teamRepo, PlayerRepo playerRepo) {
    this.teamRepo = teamRepo;
    this.playerRepo = playerRepo;
  }
}
