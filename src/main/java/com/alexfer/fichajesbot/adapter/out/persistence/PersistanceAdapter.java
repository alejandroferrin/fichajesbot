package com.alexfer.fichajesbot.adapter.out.persistence;

import com.alexfer.fichajesbot.adapter.out.persistence.model.DbEntityMapper;
import com.alexfer.fichajesbot.adapter.out.persistence.model.MovementDbEntity;
import com.alexfer.fichajesbot.adapter.out.persistence.model.UserDbEntity;
import com.alexfer.fichajesbot.application.out.PersistancePort;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersistanceAdapter implements PersistancePort {

  private final UserRepository userRepository;
  private final MovementRepository movementRepository;
  private final DbEntityMapper mapper;

  @Override
  public Optional<User> saveUser(User user) {
    return Try.of(() -> {
      UserDbEntity createdUser = userRepository.save(mapper.toUserDb(user));
      return Optional.of(mapper.toUserDomain(createdUser));
    }).getOrElse(Optional.empty());

  }

  @Override
  public Optional<User> findUserByCode(String code) {
    Optional<UserDbEntity> userOp = userRepository.findByActivationCode(code);
    return userOp.map(mapper::toUserDomain);
  }


  @Override
  public boolean saveMovement(Long userId, Movement movement) {
    return Try.of(() -> {
      Optional<UserDbEntity> userOp = userRepository.findById(userId);
      if (userOp.isEmpty()) return false;
      MovementDbEntity movementDb = mapper.toMovementDb(movement);
      movementDb.setUser(userOp.get());
      movementRepository.save(movementDb);
      return true;
    }).getOrElse(false);
  }

  @Override
  public Optional<Movement> findMovement(Long movementId) {
    return movementRepository.findById(movementId)
        .map(mapper::toMovementDomain);
  }

  @Override
  public Optional<User> findUserById(Long userId) {
    return userRepository.findById(userId)
        .map(mapper::toUserDomain);
  }

  @Override
  public boolean updateMovement(Movement movement) {
    return Try.of(() -> {
      movementRepository.save(mapper.toMovementDb(movement));
      return true;
    }).getOrElse(false);
  }

  @Override
  public List<Movement> findMovementsByUser(User user, int year, int month) {
    return movementRepository.findByUserIdAndYearAndMonth(user.getId(), year, month)
        .stream().map(mapper::toMovementDomain)
        .toList();
  }

  @Override
  public List<Movement> findMovements(int year, int month) {
    return movementRepository.findByYearAndMonth(year, month)
        .stream().map(mapper::toMovementDomain)
        .toList();
  }

  @Override
  public List<User> findAllUsers() {
    return userRepository.findAll()
        .stream().map(mapper::toUserDomain)
        .toList();
  }
}
