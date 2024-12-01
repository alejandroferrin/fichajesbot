package com.alexfer.fichajesbot.application.in;

import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;

import java.util.List;
import java.util.Map;

public interface MovementsPort {

  boolean recordMovement(Long userId, Movement movement);

  Map<User, List<Movement>>downloadMovementsByUser(Long userId, int year, int month);

  Map<User, List<Movement>> downloadMovements(int year, int month);

  boolean commentMovement(Long movementId, String comment);

}
