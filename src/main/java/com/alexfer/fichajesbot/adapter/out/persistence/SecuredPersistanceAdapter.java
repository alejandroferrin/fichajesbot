package com.alexfer.fichajesbot.adapter.out.persistence;

import com.alexfer.fichajesbot.application.out.PersistancePort;
import com.alexfer.fichajesbot.application.out.SecuredPersistancePort;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;
import com.alexfer.fichajesbot.security.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecuredPersistanceAdapter implements SecuredPersistancePort {

  private final PersistancePort persistancePort;
  private final EncryptionService encryptionService;

  public Optional<User> saveUser(User user) {
    user.setActivationCode(encryptionService.encrypt(user.getActivationCode()));
    var savedOp = persistancePort.saveUser(encryptUserSensitiveData(user));
    return savedOp.map(this::decryptUserSensitiveData);
  }

  public Optional<User> findUserByCode(String code) {
    String encoded = encryptionService.encrypt(code);
    var responseOp = persistancePort.findUserByCode(encoded);
    if (responseOp.isEmpty()) return responseOp;
    var response = responseOp.get();
    return Optional.of(decryptUserSensitiveData(response));
  }


  public boolean saveMovement(Long userId, Movement movement) {
    return persistancePort.saveMovement(userId, movement);
  }

  public Optional<Movement> findMovement(Long movementId) {
    return persistancePort.findMovement(movementId);
  }

  public Optional<User> findUserById(Long userId) {
    var responseOp = persistancePort.findUserById(userId);
    if (responseOp.isEmpty()) return responseOp;
    var response = responseOp.get();
    return Optional.of(decryptUserSensitiveData(response));

  }

  public boolean updateMovement(Movement movement) {
    return persistancePort.updateMovement(movement);
  }

  public List<Movement> findMovementsByUser(User user, int year, int month) {
    return persistancePort.findMovementsByUser(user, year, month);
  }

  public List<Movement> findMovements(int year, int month) {
    return
        persistancePort.findMovements(year, month)
            .stream().map(mov ->
                {
                  mov.setUser(decryptUserSensitiveData(mov.getUser()));
                  return mov;
                }
            )
            .toList();

  }

  public List<User> findAllUsers() {
    return persistancePort.findAllUsers()
        .stream().map(this::decryptUserSensitiveData).toList();
  }

  private User encryptUserSensitiveData(User user) {
    user.setName(encryptionService.encrypt(user.getName()));
    user.setPersonalId(encryptionService.encrypt(user.getPersonalId()));
    user.setPhone(encryptionService.encrypt(user.getPhone()));
    return user;
  }

  private User decryptUserSensitiveData(User user) {
    user.setActivationCode(encryptionService.decrypt(user.getActivationCode()));
    user.setName(encryptionService.decrypt(user.getName()));
    user.setPersonalId(encryptionService.decrypt(user.getPersonalId()));
    user.setPhone(encryptionService.decrypt(user.getName()));
    return user;
  }
}
