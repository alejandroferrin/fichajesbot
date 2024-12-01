package com.alexfer.fichajesbot.application.in.usecase;

import com.alexfer.fichajesbot.application.in.UsersPort;
import com.alexfer.fichajesbot.application.in.util.ActivationCodeGenerator;
import com.alexfer.fichajesbot.application.out.SecuredPersistancePort;
import com.alexfer.fichajesbot.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService implements UsersPort {

  private final SecuredPersistancePort persistence;

  @Override
  public Optional<User> activateUser(String activationCode) {
    return persistence
        .findUserByCode(activationCode);
  }

  @Override
  public Optional<String> createUser(User user) {
    user.setActivationCode(String.valueOf(ActivationCodeGenerator.generate()));
    var createdUserOp = persistence.saveUser(user);
    return createdUserOp.map(User::getActivationCode);
  }

  @Override
  public Optional<String> regenerateCode(Long userId) {
    var userOp = persistence.findUserById(userId);
    if (userOp.isEmpty()) return Optional.empty();
    var user = userOp.get();
    String activationCode = String.valueOf(ActivationCodeGenerator.generate());
    user.setActivationCode(activationCode);
    var createdUserOp = persistence.saveUser(user);
    if (createdUserOp.isEmpty()) return Optional.empty();
    return Optional.of(activationCode);
  }

  @Override
  public List<User> getUsers() {
    return persistence.findAllUsers();
  }

  @Override
  public Optional<User> getUser(Long targetId) {
    return persistence.findUserById(targetId);
  }

}
