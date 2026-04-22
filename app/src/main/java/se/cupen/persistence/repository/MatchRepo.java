package se.cupen.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import se.cupen.persistence.model.Match;

public interface MatchRepo extends JpaRepository<Match, UUID> {

}
