package se.cupen.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.cupen.util.MatchGroup;
import se.cupen.util.MatchResult;
import se.cupen.util.MatchType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSpecificMatchDTO {

  private String id;
  private Instant playedAt;
  private int tournamentYear;
  private TeamDTO teamA;
  private TeamDTO teamB;
  private List<MatchEventDTO> events;
  private String score;
  private MatchResult result;
  private MatchType matchType;
  private MatchGroup group;
  private Integer goalDifference;

}
