package com.alexfer.fichajesbot.application.in.secured;

import com.alexfer.fichajesbot.application.in.MovementsPort;
import com.alexfer.fichajesbot.application.in.util.AuthorizationChecker;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.Role;
import com.alexfer.fichajesbot.domain.Type;
import com.alexfer.fichajesbot.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SecuredMovementsService implements SecuredMovementsPort {

  private final MovementsPort movementsService;
  private final AuthorizationChecker authorizationChecker;


  @Override
  public boolean recordMovement(Long requesterId, Long userId, Type type) {
    final List<Role> authorizedRoles = List.of(Role.USER, Role.ADMIN);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles, requesterId)) return false;
    return movementsService.recordMovement(
        userId,
        Movement.builder()
            .type(type)
            .build()
    );
  }


  @Override
  public Map<User, List<Movement>> downloadMovementsByUser(Long requesterId, Long userId, int year, int month) {
    final List<Role> authorizedRoles = List.of(Role.ADMIN, Role.INSPECTOR, Role.USER);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles, userId)) return Map.of();
    return movementsService.downloadMovementsByUser(userId, year, month);
  }

  @Override
  public Map<User, List<Movement>> downloadMovements(Long requesterId, int year, int month) {
    final List<Role> authorizedRoles = List.of(Role.ADMIN, Role.INSPECTOR);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles)) return Map.of();
    return movementsService.downloadMovements(year, month);
  }

  @Override
  public boolean commentMovement(Long requesterId, Long movementId, String comment) {
    final List<Role> authorizedRoles = List.of(Role.ADMIN);
    if (authorizationChecker.userIsNotAuthorized(requesterId, authorizedRoles)) return false;
    return movementsService.commentMovement(movementId, comment);
  }

}
