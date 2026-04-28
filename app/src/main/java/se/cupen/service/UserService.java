package se.cupen.service;

import org.springframework.stereotype.Service;

import se.cupen.persistence.repository.UserRepo;

@Service
public class UserService {

  private final UserRepo userRepo;

  public UserService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

}
