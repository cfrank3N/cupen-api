package se.cupen.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import se.cupen.persistence.model.Player;

public interface PlayerRepo extends JpaRepository<Player, UUID> {

}
