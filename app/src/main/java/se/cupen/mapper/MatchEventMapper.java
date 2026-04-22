package se.cupen.mapper;

import se.cupen.dto.MatchEventDTO;
import se.cupen.persistence.model.MatchEvent;

public class MatchEventMapper {

  public static MatchEventDTO toDTO(MatchEvent matchEvent) {
    return MatchEventDTO.builder()
        .id(matchEvent.getId().toString())
        .player(PlayerMapper.toDTO(matchEvent.getPlayer()))
        .type(matchEvent.getType())
        .build();
  }
}
