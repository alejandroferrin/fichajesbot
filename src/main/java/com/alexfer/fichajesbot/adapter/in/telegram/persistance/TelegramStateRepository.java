package com.alexfer.fichajesbot.adapter.in.telegram.persistance;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.TelegramState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramStateRepository extends JpaRepository<TelegramState, Long> {

  Optional<TelegramState> findByChatId(Long chatId);

}
