package se.cupen.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import se.cupen.persistence.model.MatchEvent;

public interface MatchEventRepo extends JpaRepository<MatchEvent, UUID> {

  Optional<List<MatchEvent>> findAllByPlayerId(UUID playerId);

}
