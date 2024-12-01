package com.alexfer.fichajesbot.application.in;

import com.alexfer.fichajesbot.domain.User;

import java.util.List;
import java.util.Optional;

public interface UsersPort {

  Optional<User> activateUser(String activationCode);

  Optional<String> createUser(User user);

  Optional<String> regenerateCode(Long userId);

  List<User> getUsers();


  Optional<User> getUser(Long targetId);
}
