package se.cupen.persistence.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class Player {

  @Id
  @UuidGenerator
  private UUID id;
  private String name;
  private String city;
  private long pricemoney;
  private Integer rating;

  @ManyToMany(mappedBy = "players")
  @Builder.Default
  private List<Team> teams = new ArrayList<>();

}
