package se.cupen.persistence.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
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
public class User {

  // TODO: Eventually add roles to the user to make everyone be able to log in

  @Id
  @UuidGenerator
  private UUID id;

  @Column(unique = true)
  private String username;

  private String password;

}
