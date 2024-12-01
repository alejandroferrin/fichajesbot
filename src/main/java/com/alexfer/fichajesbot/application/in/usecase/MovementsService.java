package com.alexfer.fichajesbot.application.in.usecase;

import com.alexfer.fichajesbot.application.in.MovementsPort;
import com.alexfer.fichajesbot.application.out.SecuredPersistancePort;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovementsService implements MovementsPort {

  private final SecuredPersistancePort persistence;

  @Override
  public boolean recordMovement(Long userId, Movement movement) {
    movement.setTime(LocalDateTime.now());
    return persistence.saveMovement(userId, movement);
  }

  @Override
  public Map<User, List<Movement>> downloadMovementsByUser(Long userId, int year, int month) {
    return persistence.findUserById(userId)
        .map(
            user ->
                Map.of(
                    user,
                    persistence.findMovementsByUser(user, year, month)
                )
        )
        .orElse(Map.of());
  }

  @Override
  public Map<User, List<Movement>> downloadMovements(int year, int month) {
    return persistence.findMovements(year, month)
        .stream().collect(Collectors.groupingBy(Movement::getUser));

  }

  @Override
  public boolean commentMovement(Long movementId, String comment) {
    AtomicBoolean response = new AtomicBoolean(false);
    persistence
        .findMovement(movementId)
        .ifPresent(
            movement -> {
              movement.setComment(comment);
              boolean updateResult = persistence.updateMovement(movement);
              response.set(updateResult);
            }
        );
    return response.get();
  }
}
