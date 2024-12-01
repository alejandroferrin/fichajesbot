package com.alexfer.fichajesbot.adapter.in.telegram;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.DownloadType;
import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.TelegramState;
import com.alexfer.fichajesbot.adapter.in.telegram.util.Commands;
import com.alexfer.fichajesbot.adapter.in.telegram.util.MenuFactory;
import com.alexfer.fichajesbot.adapter.in.telegram.util.MessageUtils;
import com.alexfer.fichajesbot.domain.Role;
import com.alexfer.fichajesbot.domain.Type;
import com.alexfer.fichajesbot.domain.User;
import com.alexfer.fichajesbot.util.DateTimeFormatters;
import com.alexfer.fichajesbot.util.FileNameFactory;
import com.alexfer.fichajesbot.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
@Slf4j
public class TelegramAdapter extends TelegramLongPollingBot {

  private static final String NO_SE_PUDO_INICIAR_EL_CHAT = "No se pudo iniciar el chat";
  private static final String CHAT_NO_ACTIVADO = "Chat no activado.";
  private static final String DEBES_ESPERAR_UN_MINUTO_PARA_VOLVER_A_FICHAR = "Debes esperar un minuto para volver a fichar";
  private static final String FICHAJE_REALIZADO = "Fichaje realizado";
  private static final String NO_SE_PUDO_REALIZAR_EL_FICHAJE = "No se pudo realizar el fichaje";
  private static final String MARKDOWN = "markdown";
  private static final String FICHAJES = "fichajes";
  private static final String EXTENSION = ".csv";
  private static final String SPACE = " ";
  private final TelegramStateMachine stateMachine;
  private final FlowsStarter flowsStarter;
  private final String botName;
  private final int chunkLength;

  public TelegramAdapter(
      TelegramStateMachine stateMachine,
      FlowsStarter flowsStarter,
      @Value("${telegram.bot.name:my_coding_assistant_bot}") String botName,
      @Value("${telegram.chunk.length:4096}") int chunkLength,
      @Value("${telegram.bot.token}") String token
  ) {
    super(token);
    this.stateMachine = stateMachine;
    this.flowsStarter = flowsStarter;
    this.botName = botName;
    this.chunkLength = chunkLength;
  }


  @Override
  public String getBotUsername() {
    return botName;
  }


  @Override
  public void onUpdateReceived(Update update) {

    var chatId = MessageUtils.getChatId(update);

    if (MessageUtils.isCallbackQueryFlow(update)) followCallBackFlow(update, chatId);
    else if (MessageUtils.isCommandFlow(update)) followCommandFlow(update, chatId);
    else if (MessageUtils.isStateFlow(update)) followStateMachineFlow(update, chatId);

  }

  private void followCallBackFlow(Update update, long chatId) {
    var stateOp = getState(chatId);
    if (stateOp.isEmpty()) {
      sendSimpleTextMessage(chatId, NO_SE_PUDO_INICIAR_EL_CHAT);
      return;
    }
    var state = stateOp.get();
    handleCallBack(update, state, chatId);
  }

  private void followStateMachineFlow(Update update, long chatId) {
    var stateOp = getState(chatId);
    if (stateOp.isEmpty()) {
      sendSimpleTextMessage(chatId, NO_SE_PUDO_INICIAR_EL_CHAT);
      return;
    }
    var state = stateOp.get();
    var textMessage = update.getMessage().getText();
    var processStateResult = stateMachine.process(state, textMessage);
    if (Objects.equals(DownloadType.PANTALLA, processStateResult.type())) {
      sendSimpleTextMessage(chatId, processStateResult.message());
    } else if (Objects.equals(DownloadType.DOCUMENTO, processStateResult.type())) {
      sendDocument(chatId, processStateResult.message());
    }
  }

  private void followCommandFlow(Update update, long chatId) {
    var stateOp = getState(chatId);
    if (stateOp.isEmpty()) {
      sendSimpleTextMessage(chatId, NO_SE_PUDO_INICIAR_EL_CHAT);
      return;
    }
    var state = stateOp.get();
    if (MessageUtils.isNotActivatedAndCommandIsNotStart(update, state)) {
      sendSimpleTextMessage(chatId, CHAT_NO_ACTIVADO);
      return;
    }
    processCommands(update, chatId, state);
  }

  private Optional<TelegramState> getState(long chatId) {
    var stateOp = stateMachine.getState(chatId);
    if (stateOp.isEmpty()) stateOp = stateMachine.registerNewChat(chatId);
    return stateOp;
  }


  private void processCommands(Update update, long chatId, TelegramState state) {
    if (Commands.isStartCommand(update)) {
      sendSimpleTextMessage(chatId, flowsStarter.startChatActivation(state));
    } else if (Commands.isMenuCommand(update)) {
      printMainMenu(chatId, state);
      stateMachine.setNoState(chatId);
    }

  }

  private void printMainMenu(long chatId, TelegramState state) {
    if (Objects.equals(state.getUserRole(), Role.ADMIN.name())) {
      SendMessage userMainMenu = MenuFactory.getUserMainMenu(chatId);
      sendMenu(userMainMenu);
      SendMessage adminMainMenu = MenuFactory.getAdminMainMenu(chatId);
      sendMenu(adminMainMenu);
    } else if (Objects.equals(state.getUserRole(), Role.INSPECTOR.name())) {
      SendMessage inspectorMainMenu = MenuFactory.getInspectorMainMenu(chatId);
      sendMenu(inspectorMainMenu);
    } else {
      SendMessage userMainMenu = MenuFactory.getUserMainMenu(chatId);
      sendMenu(userMainMenu);
    }
  }


  private void sendDocument(long chatId, String msg) {
    SendDocument document = new SendDocument();
    document.setChatId(chatId);
    document.setDocument(new InputFile(StreamUtils.getInputStreamFromString(msg), FileNameFactory.create(FICHAJES, EXTENSION)));
    executeAsyncDocument(document);
  }


  private void sendSimpleTextMessage(Long chatId, String msg) {
    MessageUtils.chopText(msg, chunkLength).forEach(chunk -> {
      SendMessage message = new SendMessage(); // Create a message object
      message.setChatId(chatId);
      message.setText(chunk);
      //message.setParseMode(MARKDOWN);
      executeAsyncTextMessasge(message);
    });
  }

  private void handleCallBack(Update update, TelegramState state, long chatId) {
    // Set variables
    String callData = update.getCallbackQuery().getData();
    //analyze callback
    switch (callData) {
      case MenuFactory.ENTRAR_CALLBACK -> processEntrarCallback(state, chatId);
      case MenuFactory.SALIR_CALLBACK -> processSalirCallback(state, chatId);
      case MenuFactory.RESUMEN_CALLBACK -> sendSimpleTextMessage(chatId, flowsStarter.startOwnSummary(state));
      case MenuFactory.ALTA_CALLBACK -> sendSimpleTextMessage(chatId, flowsStarter.startAlta(state));
      case MenuFactory.USUARIOS_CALLBACK -> processarUsuariosCallback(state, chatId);
      case MenuFactory.RESUMEN_USUARIO_CALLBACK -> sendSimpleTextMessage(chatId, flowsStarter.startUserSummary(state));
      case MenuFactory.RESUMEN_TODOS_CALLBACK -> sendSimpleTextMessage(chatId, flowsStarter.startAllSummary(state));
      case MenuFactory.COMENTAR_FICHAJE_CALLBACK -> sendSimpleTextMessage(chatId, flowsStarter.startCommentMovement(state));
      case MenuFactory.RESTABLECER_USUARIO_CALLBACK -> sendSimpleTextMessage(chatId, flowsStarter.startResetUser(state));
      default -> sendSimpleTextMessage(chatId, EMPTY);
    }

  }

  private void processarUsuariosCallback(TelegramState state, long chatId) {
    sendSimpleTextMessage(
        chatId,
        stateMachine.getUsers(state)
            .stream()
            .map(User::toString)
            .collect(Collectors.joining("\n"))
    );
  }

  private void processSalirCallback(TelegramState state, long chatId) {
    var now = LocalDateTime.now();
    if (Duration.between(state.getLastMovement(), now).toMinutes() < 1) {
      sendSimpleTextMessage(chatId, DEBES_ESPERAR_UN_MINUTO_PARA_VOLVER_A_FICHAR);
      return;
    }
    if (stateMachine.clockOut(state))
      sendSimpleTextMessage(chatId, FICHAJE_REALIZADO + SPACE + Type.SALIDA + SPACE + now.format(DateTimeFormatters.getDateTimeFormatterESP()));
    else sendSimpleTextMessage(chatId, NO_SE_PUDO_REALIZAR_EL_FICHAJE);
  }

  private void processEntrarCallback(TelegramState state, long chatId) {
    var now = LocalDateTime.now();
    if (Duration.between(state.getLastMovement(), now).toMinutes() < 1) {
      sendSimpleTextMessage(chatId, DEBES_ESPERAR_UN_MINUTO_PARA_VOLVER_A_FICHAR);
      return;
    }
    if (stateMachine.clockIn(state))
      sendSimpleTextMessage(chatId, FICHAJE_REALIZADO + SPACE + Type.ENTRADA + SPACE + now.format(DateTimeFormatters.getDateTimeFormatterESP()));
    else sendSimpleTextMessage(chatId, NO_SE_PUDO_REALIZAR_EL_FICHAJE);
  }


  private void executeAsyncDocument(SendDocument document) {
    try {
      executeAsync(document);
    } catch (Exception e) {
      //nothing to do
    }
  }

  private void executeAsyncTextMessasge(SendMessage msg) {
    try {
      executeAsync(msg);
    } catch (Exception e) {
      //nothing to do
    }
  }

  private void sendMenu(SendMessage msg) {
    try {
      msg.setParseMode(MARKDOWN);
      executeAsync(msg);
    } catch (TelegramApiException e) {
      //nothing to do
    }
  }


}
