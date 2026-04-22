package se.cupen.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import se.cupen.persistence.model.Tournament;

public interface TournamentRepo extends JpaRepository<Tournament, UUID> {

}
