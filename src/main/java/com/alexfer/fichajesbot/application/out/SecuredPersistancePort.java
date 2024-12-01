package com.alexfer.fichajesbot.application.out;

import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;

import java.util.List;
import java.util.Optional;

public interface SecuredPersistancePort {

  Optional<User> saveUser(User user);

  Optional<User> findUserByCode(String code);

  Optional<User> findUserById(Long userId);

  boolean saveMovement(Long userId, Movement movement);

  Optional<Movement> findMovement(Long movementId);

  boolean updateMovement(Movement movement);

  List<Movement> findMovementsByUser(User user, int year, int month);

  List<Movement> findMovements(int year, int month);

  List<User> findAllUsers();

}
