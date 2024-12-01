package com.alexfer.fichajesbot.application.in.util;

import com.alexfer.fichajesbot.application.out.PersistancePort;
import com.alexfer.fichajesbot.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthorizationChecker {

  private final PersistancePort persistence;

  public boolean userIsNotAuthorized(Long requesterId, List<Role> authorizedRoles) {
    var requesterOp = persistence.findUserById(requesterId);
    return requesterOp.isEmpty() ||
        !authorizedRoles.contains(requesterOp.get().getRole());
  }

  public boolean userIsNotAuthorized(Long requesterId, List<Role> authorizedRoles, Long targetUserId) {
    var requesterOp = persistence.findUserById(requesterId);
    if (requesterOp.isEmpty()) return true;
    boolean userDoesNotHaveReqRole = !authorizedRoles.contains(requesterOp.get().getRole());
    boolean userCannotChangeTargetUser =
        Objects.equals(Role.USER, requesterOp.get().getRole()) &&
            !Objects.equals(requesterId, targetUserId);
    return userDoesNotHaveReqRole || userCannotChangeTargetUser;
  }

  public boolean userIsNotAuthorizedChangeState(Long requesterId, Long targetUserId) {
    var requesterOp = persistence.findUserById(requesterId);
    if (requesterOp.isEmpty()) return true;
    if (requesterOp.get().getRole() == Role.INSPECTOR) return true;
    return !Objects.equals(requesterId, targetUserId);
  }


}
