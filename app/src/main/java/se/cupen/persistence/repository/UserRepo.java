package se.cupen.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import se.cupen.persistence.model.User;

public interface UserRepo extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

}
