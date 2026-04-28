package se.cupen.service;

import org.springframework.stereotype.Service;

import se.cupen.exception.ValidationException;
import se.cupen.persistence.model.User;
import se.cupen.persistence.repository.UserRepo;

@Service
public class UserService {

  private final UserRepo userRepo;

  public UserService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  public User findByUsername(String username) {
    return userRepo.findByUsername(username)
        .orElseThrow(() -> new ValidationException("User not found", 404));
  }

}
