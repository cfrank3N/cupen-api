package se.cupen.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import se.cupen.persistence.model.Team;

public interface TeamRepo extends JpaRepository<Team, UUID> {

}
