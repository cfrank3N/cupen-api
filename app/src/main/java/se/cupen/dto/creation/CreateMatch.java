package se.cupen.dto.creation;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.cupen.util.MatchGroup;
import se.cupen.util.MatchType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMatch {
  private String teamAId;
  private String teamBId;
  private Instant playedAt;
  private MatchType matchType;
  private MatchGroup matchGroup;
}
