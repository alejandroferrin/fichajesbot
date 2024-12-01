package com.alexfer.fichajesbot.adapter.in.telegram.persistance;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.TelegramState;
import com.alexfer.fichajesbot.security.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TelegramPersistanceAdapter {

  private final TelegramStateRepository repository;
  private final EncryptionService encryptionService;

  public Optional<TelegramState> getState(long chatId) {
    return repository.findByChatId(chatId)
        .map(state -> {
          state.setFlowUserName(encryptionService.decrypt(state.getFlowUserName()));
          state.setFlowUserPersonalId(encryptionService.decrypt(state.getFlowUserPersonalId()));
          state.setFlowUserPhone(encryptionService.decrypt(state.getFlowUserPhone()));
          return state;
        });
  }

  public TelegramState save(TelegramState entity) {
    entity.setFlowUserName(encryptionService.encrypt(entity.getFlowUserName()));
    entity.setFlowUserPersonalId(encryptionService.encrypt(entity.getFlowUserPersonalId()));
    entity.setFlowUserPhone(encryptionService.encrypt(entity.getFlowUserPhone()));
    return repository.save(entity);
  }
}
