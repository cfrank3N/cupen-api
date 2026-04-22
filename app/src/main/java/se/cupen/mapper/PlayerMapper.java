package se.cupen.mapper;

import se.cupen.dto.PlayerDTO;
import se.cupen.persistence.model.Player;

public class PlayerMapper {

  public static PlayerDTO toDTO(Player player) {
    return PlayerDTO.builder()
        .id(player.getId().toString())
        .name(player.getName())
        .city(player.getCity())
        .pricemoney(player.getPricemoney())
        .rating(player.getRating())
        .build();
  }
}
