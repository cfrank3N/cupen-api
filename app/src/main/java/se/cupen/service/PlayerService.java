package se.cupen.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.cupen.dto.PlayerDTO;
import se.cupen.mapper.PlayerMapper;
import se.cupen.persistence.repository.PlayerRepo;

@Service
public class PlayerService {

  private final PlayerRepo playerRepo;

  public PlayerService(PlayerRepo playerRepo) {
    this.playerRepo = playerRepo;
  }

  public List<PlayerDTO> findAllPlayers() {
    return playerRepo.findAll().stream().map(player -> PlayerMapper.toDTO(player)).toList();
  }
}
