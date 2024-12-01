package com.alexfer.fichajesbot.adapter.in.telegram.util;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.TelegramState;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class MessageUtils {


  public static List<String> chopText(String text, int chunkLength) {
    List<String> chunks = new ArrayList<>();
    int index = 0;

    while (index < text.length()) {
      if (index + chunkLength <= text.length()) {
        chunks.add(text.substring(index, index + chunkLength));
        index += chunkLength;
      } else {
        chunks.add(text.substring(index));
        index = text.length();
      }
    }
    return chunks;
  }


  public static long getChatId(Update update) {
    var callbackQueryChatId = Optional.ofNullable(update)
        .map(Update::getCallbackQuery)
        .map(CallbackQuery::getMessage)
        .map(MaybeInaccessibleMessage::getChatId);
    if (callbackQueryChatId.isPresent()) return callbackQueryChatId.get();
    var messageChatId = Optional.ofNullable(update)
        .map(Update::getMessage)
        .map(Message::getChatId);
    if (messageChatId.isPresent()) return messageChatId.get();
    var fromChatId = Optional.ofNullable(update)
        .map(Update::getChatMember)
        .map(ChatMemberUpdated::getFrom)
        .map(User::getId);
    return fromChatId.orElse(0L);
  }

  public static boolean isStateFlow(Update update) {
    return update.getMessage() != null && update.getMessage().hasText() && !update.getMessage().getFrom().getIsBot();
  }

  public static boolean isCommandFlow(Update update) {
    return update.getMessage() != null && update.getMessage().isCommand();
  }

  public static boolean isCallbackQueryFlow(Update update) {
    return update.hasCallbackQuery();
  }

  public static boolean isNotActivatedAndCommandIsNotStart(Update update, TelegramState state) {
    return !state.isActivated() && !Commands.isStartCommand(update);
  }
}
