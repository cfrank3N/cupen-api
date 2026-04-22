package se.cupen.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchDTO {

  private String id;
  private Instant playedAt;
  private int tournamentYear;
  private TeamDTO teamA;
  private TeamDTO teamB;
  private List<MatchEventDTO> events;
  private String result;

}
