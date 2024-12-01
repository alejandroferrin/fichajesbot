package com.alexfer.fichajesbot.application.in.secured;

import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.Type;
import com.alexfer.fichajesbot.domain.User;

import java.util.List;
import java.util.Map;

public interface SecuredMovementsPort {

  boolean recordMovement(Long requesterId, Long userId, Type type);

  Map<User, List<Movement>> downloadMovementsByUser(Long requesterId, Long userId, int year, int month);

  Map<User, List<Movement>> downloadMovements(Long requesterId, int year, int month);

  boolean commentMovement(Long requesterId, Long movementId, String comment);

}
