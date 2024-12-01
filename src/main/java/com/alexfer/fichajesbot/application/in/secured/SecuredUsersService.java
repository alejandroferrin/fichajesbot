package com.alexfer.fichajesbot.application.in.secured;

import com.alexfer.fichajesbot.application.in.UsersPort;
import com.alexfer.fichajesbot.application.in.util.AuthorizationChecker;
import com.alexfer.fichajesbot.domain.Role;
import com.alexfer.fichajesbot.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecuredUsersService implements SecuredUsersPort {
  private final UsersPort usersService;
  private final AuthorizationChecker authorizationChecker;

  @Override
  public Optional<User> activateUser(String activationCode) {
    return usersService.activateUser(activationCode);
  }

  @Override
  public Optional<String> createUser(Long requesterId, User user) {
    final List<Role> authorizedRoles = List.of(Role.ADMIN);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles)) return Optional.empty();
    return usersService.createUser(user);
  }

  @Override
  public Optional<String> regenerateCode(Long requesterId, Long userId) {
    final List<Role> authorizedRoles = List.of(Role.ADMIN, Role.USER);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles, userId)) return Optional.empty();
    return usersService.regenerateCode(userId);
  }

  @Override
  public List<User> getUsers(Long requesterId) {
    final List<Role> authorizedRoles = List.of(Role.ADMIN, Role.INSPECTOR);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles)) return List.of();
    return usersService.getUsers();
  }

  @Override
  public Optional<User> getUser(Long requesterId, Long targetId) {
    final List<Role> authorizedRoles = List.of(Role.ADMIN, Role.INSPECTOR);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles)) return Optional.empty();
    return usersService.getUser(targetId);
  }

}
