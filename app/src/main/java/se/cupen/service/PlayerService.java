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
  private final MediaUploadService mediaService;

  public PlayerService(PlayerRepo playerRepo, MediaUploadService mediaService) {
    this.playerRepo = playerRepo;
    this.mediaService = mediaService;
  }

  public ResponseData<List<PlayerDTO>> findAllPlayers() {
    List<PlayerDTO> players = playerRepo.findAll().stream().map(player -> PlayerMapper.toDTO(player)).toList();
    return ResponseData.successful(players, "All players fetched");
  }

  public ResponseData<PlayerDTO> insertPlayer(CreatePlayer player) {

    Player playerToCreate = Player.builder()
        .city(player.getCity())
        .name(player.getName())
        .build();

    // Save image via mediaService and set returned value from operation as imageURL
    if (player.getImage() != null && !player.getImage().isEmpty()) {
      playerToCreate.setImageUrl(mediaService.uploadImage(player.getImage()));
    } else {
      playerToCreate.setImageUrl(
          "https://res.cloudinary.com/drrwrnzjk/image/upload/q_auto/f_auto/v1778501155/vector-flat-illustration-grayscale-avatar-user-profile-person-icon-gender-neutral-silhouette-profile-picture-suitable-social-media-profiles-icons-screensavers-as-templatex9xa_719432-2191_nb5gk9.avif");
    }

    PlayerDTO createdPlayer = PlayerMapper.toDTO(playerRepo.save(playerToCreate));

    return ResponseData.successful(createdPlayer, "Player created");
  }
}
