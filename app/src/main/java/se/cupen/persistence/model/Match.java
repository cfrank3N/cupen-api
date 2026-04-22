package se.cupen.persistence.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Match {

  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  private Tournament tournament;

  @ManyToOne
  private Team teamA;

  @ManyToOne
  private Team teamB;

  @OneToMany(mappedBy = "match", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private List<MatchEvent> events = new ArrayList<>();

  private Instant playedAt;

}
