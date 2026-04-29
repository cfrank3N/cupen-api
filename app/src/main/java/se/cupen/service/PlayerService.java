package se.cupen.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.cupen.dto.PlayerDTO;
import se.cupen.dto.creation.CreatePlayer;
import se.cupen.mapper.PlayerMapper;
import se.cupen.persistence.model.Player;
import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.util.ResponseData;

@Service
public class PlayerService {

  private final PlayerRepo playerRepo;

  public PlayerService(PlayerRepo playerRepo) {
    this.playerRepo = playerRepo;
  }

  public List<PlayerDTO> findAllPlayers() {
    return playerRepo.findAll().stream().map(player -> PlayerMapper.toDTO(player)).toList();
  }

  public ResponseData<List<PlayerDTO>> insertPlayers(List<CreatePlayer> players) {

    List<Player> playersToCreate = players.stream().map(PlayerMapper::fromCreationDTO).toList();

    List<PlayerDTO> createdPlayers = playerRepo.saveAll(playersToCreate).stream().map(PlayerMapper::toDTO).toList();

    return ResponseData.successful(createdPlayers, "Players created");
  }
}
