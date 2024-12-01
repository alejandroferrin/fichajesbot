package com.alexfer.fichajesbot.adapter.in.telegram;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.TelegramPersistanceAdapter;
import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.DownloadType;
import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.Flow;
import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.TelegramState;
import com.alexfer.fichajesbot.application.in.secured.SecuredMovementsPort;
import com.alexfer.fichajesbot.application.in.secured.SecuredUsersPort;
import com.alexfer.fichajesbot.document.DocumentTemplate;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.Role;
import com.alexfer.fichajesbot.domain.Type;
import com.alexfer.fichajesbot.domain.User;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramStateMachine {

  private static final String NO_SE_PUDO_PROCESAR_EL_DATO = "Lo siento no te he entendido, repite el dato.";
  private static final String NO_SE_PUDO_PROCESAR_LA_ACCION = "No se pudo procesar la acción";
  private static final String LO_HAS_INTENTADO_DEMASIADAS_VECES_DEBERAS_VOLVER_A_EMPEZAR = "Lo has intentado demasiadas veces, deberás volver a empezar.";
  private static final String HAS_SUPERADO_EL_LIMITE_DE_INTENTOS_DE_ACTIVACION = "Has superado el límite de intentos de activación.";
  private static final String CODIGO_NO_VALIDO = "Código no válido";
  private static final String CHAT_ACTIVADO = "Chat activado";
  private static final String NO_HAY_FICHAJES = "No hay fichajes";
  private static final String SIN_DATOS = "Sin datos";
  private static final String NO_SE_PUDO_CREAR_EL_USUARIO = "No se pudo crear el usuario";
  private static final String EL_CODIGO_DE_ACTIVACION_ES = "El Código de activación es: ";
  private static final String NO_SE_PUDO_COMENTAR_EL_FICHAJE = "No se pudo comentar el fichaje";
  private static final String COMENTARIO_INSERTADO = "Comentario insertado";
  public static final String NO_SE_PUDO_GENERAR_EL_NUEVO_CODIGO = "No se pudo generar el nuevo Código";
  public static final String NUEVO_CODIGO = "Nuevo código: ";
  public static final String NO_TIENES_PERMISOS_PARA_EJECUTAR_ESTA_ACCION = "No tienes permisos para ejecutar esta acción";
  private final SecuredMovementsPort movementsService;
  private final SecuredUsersPort usersService;
  private final TelegramPersistanceAdapter telegramPersistance;
  private final Set<DocumentTemplate> documentTemplates;
  private static final int MAX_ACTIVATION_TRIES = 5;
  private static final int MAX_FLOW_TRIES = 5;


  public StateMachineResponse process(TelegramState state, String message) {

    switch (state.getCurrentFlow()) {
      case NO_STATE -> {
        return new StateMachineResponse(EMPTY, DownloadType.NONE);
      }
      case ACTIVATE_CHAT -> {
        return processActivateChatFlow(state, message);
      }
      case DOWNLOAD_OWN_MOVEMENTS -> {
        return processDownloadOwnMovementsFlow(state, message);
      }
      case DOWNLOAD_ALL_MOVEMENTS -> {
        return processDownloadAllMovementsFlow(state, message);
      }
      case DOWNLOAD_USER_MOVEMENTS -> {
        return processDownloadUserMovementsFlow(state, message);
      }
      case COMMENT_MOVEMENT -> {
        return processCommentMovementFlow(state, message);
      }
      case CREATING_USER -> {
        return processCreatingUserFlow(state, message);
      }
      case RESET_USER -> {
        return processResetUserFlow(state, message);
      }
      default -> {
        return new StateMachineResponse(NO_SE_PUDO_PROCESAR_LA_ACCION, DownloadType.PANTALLA);
      }
    }
  }

  private StateMachineResponse processResetUserFlow(TelegramState state, String message) {
    switch (state.getCurrentSubFlow()) {
      case ASKING_USER_ID -> {
        return processAkingUserIdForResetSubFlow(state, message);
      }
      default -> {
        return new StateMachineResponse(NO_SE_PUDO_PROCESAR_LA_ACCION, DownloadType.PANTALLA);
      }
    }
  }

  private StateMachineResponse processAkingUserIdForResetSubFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    var idTry = Try.of(() -> Long.valueOf(message));
    if (idTry.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_PROCESAR_EL_DATO, DownloadType.PANTALLA);
    }
    Optional<String> activationCodeOp = usersService.regenerateCode(state.getTelegramUserId(), idTry.get());
    if (activationCodeOp.isEmpty())
      return new StateMachineResponse(NO_SE_PUDO_GENERAR_EL_NUEVO_CODIGO, DownloadType.PANTALLA);
    resetFlowAndSave(state);
    return new StateMachineResponse(NUEVO_CODIGO + activationCodeOp.get(), DownloadType.PANTALLA);
  }

  private StateMachineResponse processCommentMovementFlow(TelegramState state, String message) {
    switch (state.getCurrentSubFlow()) {
      case ASKING_MOVEMENT_ID -> {
        return processAkingMovementIdSubFlow(state, message);
      }
      case ASKING_MOV_COMMENT -> {
        return processAkingCommentSubFlow(state, message);
      }
      default -> {
        return new StateMachineResponse(NO_SE_PUDO_PROCESAR_LA_ACCION, DownloadType.PANTALLA);
      }
    }
  }

  private StateMachineResponse processAkingCommentSubFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    var commentResultSucced = movementsService.commentMovement(state.getTelegramUserId(), state.getFlowMovementId(), message);
    if (commentResultSucced) {
      resetFlowAndSave(state);
      return new StateMachineResponse(COMENTARIO_INSERTADO, DownloadType.PANTALLA);
    }
    return new StateMachineResponse(NO_SE_PUDO_COMENTAR_EL_FICHAJE, DownloadType.PANTALLA);
  }

  private StateMachineResponse processAkingMovementIdSubFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    var idTry = Try.of(() -> Long.valueOf(message));
    if (idTry.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_PROCESAR_EL_DATO, DownloadType.PANTALLA);
    }
    state.setFlowMovementId(idTry.get());
    advanceSubFlowAndSave(state);
    return new StateMachineResponse(state.getCurrentSubFlow().getMessage(), DownloadType.PANTALLA);
  }

  private StateMachineResponse processDownloadAllMovementsFlow(TelegramState state, String message) {
    setAllUsers(state, true);
    switch (state.getCurrentSubFlow()) {
      case ASKING_YEAR -> {
        return processAkingYearSubFlow(state, message);
      }
      case ASKING_MONTH -> {
        return processAkingMonthSubFlow(state, message);
      }
      case ASKING_DOWNLOAD_TYPE -> {
        return processAkingDownloadTypeSubFlow(state, message);
      }
      default -> {
        return new StateMachineResponse(NO_SE_PUDO_PROCESAR_LA_ACCION, DownloadType.PANTALLA);
      }
    }
  }


  private StateMachineResponse processCreatingUserFlow(TelegramState state, String message) {


    switch (state.getCurrentSubFlow()) {
      case ASKING_ROLE -> {
        return processAskingRoleFlow(state, message);
      }
      case ASKING_NAME -> {
        return processAskingNameFlow(state, message);
      }
      default -> {
        return new StateMachineResponse(NO_SE_PUDO_PROCESAR_LA_ACCION, DownloadType.PANTALLA);
      }
    }
  }

  private StateMachineResponse processAskingNameFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    var activationCodeOp = usersService.createUser(
        state.getTelegramUserId(),
        User.builder()
            .role(Role.getFromName(state.getFlowUserRole()).orElse(Role.USER))
            .name(message)
            .build()
    );
    if (activationCodeOp.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_CREAR_EL_USUARIO, DownloadType.PANTALLA);
    }
    resetFlowAndSave(state);
    return new StateMachineResponse(EL_CODIGO_DE_ACTIVACION_ES + activationCodeOp.get(), DownloadType.PANTALLA);
  }

  private StateMachineResponse processAskingRoleFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    var roleOp = Role.getFromName(message.toUpperCase());
    if (roleOp.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_PROCESAR_EL_DATO, DownloadType.PANTALLA);
    }
    state.setFlowUserRole(roleOp.get().name());
    advanceSubFlowAndSave(state);
    return new StateMachineResponse(state.getCurrentSubFlow().getMessage(), DownloadType.PANTALLA);
  }

  private StateMachineResponse processDownloadUserMovementsFlow(TelegramState state, String message) {
    setAllUsers(state, false);
    switch (state.getCurrentSubFlow()) {
      case ASKING_USER_ID -> {
        return processAkingUserIdSubFlow(state, message);
      }
      case ASKING_YEAR -> {
        return processAkingYearSubFlow(state, message);
      }
      case ASKING_MONTH -> {
        return processAkingMonthSubFlow(state, message);
      }
      case ASKING_DOWNLOAD_TYPE -> {
        return processAkingDownloadTypeSubFlow(state, message);
      }
      default -> {
        return new StateMachineResponse(NO_SE_PUDO_PROCESAR_LA_ACCION, DownloadType.PANTALLA);
      }
    }
  }


  private StateMachineResponse processDownloadOwnMovementsFlow(TelegramState state, String message) {
    state.setFlowTargetUserId(state.getTelegramUserId());
    setAllUsers(state, false);
    switch (state.getCurrentSubFlow()) {
      case ASKING_YEAR -> {
        return processAkingYearSubFlow(state, message);
      }
      case ASKING_MONTH -> {
        return processAkingMonthSubFlow(state, message);
      }
      case ASKING_DOWNLOAD_TYPE -> {
        return processAkingDownloadTypeSubFlow(state, message);
      }
      default -> {
        return new StateMachineResponse(NO_SE_PUDO_PROCESAR_LA_ACCION, DownloadType.PANTALLA);
      }
    }
  }


  private StateMachineResponse processActivateChatFlow(TelegramState state, String message) {
    if (adminBlocking(state))
      return new StateMachineResponse(NO_TIENES_PERMISOS_PARA_EJECUTAR_ESTA_ACCION, DownloadType.PANTALLA);
    if (state.getActivationTries() >= MAX_ACTIVATION_TRIES)
      return new StateMachineResponse(HAS_SUPERADO_EL_LIMITE_DE_INTENTOS_DE_ACTIVACION, DownloadType.PANTALLA);
    var userOp = usersService.activateUser(message);
    if (userOp.isEmpty()) {
      state.setActivationTries(state.getActivationTries() + 1);
      telegramPersistance.save(state);
      return new StateMachineResponse(CODIGO_NO_VALIDO, DownloadType.PANTALLA);
    }
    var user = userOp.get();
    state.setTelegramUserId(user.getId());
    state.setActivated(true);
    state.setCurrentFlow(Flow.NO_STATE);
    state.setUserRole(user.getRole().name());
    telegramPersistance.save(state);
    return new StateMachineResponse(CHAT_ACTIVADO, DownloadType.PANTALLA);
  }


  private StateMachineResponse processAkingDownloadTypeSubFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    var downloadTypeOp = DownloadType.getFromName(message.toUpperCase());
    if (downloadTypeOp.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_PROCESAR_EL_DATO, DownloadType.PANTALLA);
    }
    Map<User, List<Movement>> userListMap;
    if (Boolean.TRUE.equals(state.getIsForAllUsers())) {
      userListMap = movementsService.downloadMovements(state.getTelegramUserId(), state.getFlowMovementYear(), state.getFlowMovementMonth());
    } else {
      userListMap = movementsService.downloadMovementsByUser(state.getTelegramUserId(), state.getFlowTargetUserId(), state.getFlowMovementYear(), state.getFlowMovementMonth());
    }
    if (userListMap.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(SIN_DATOS, DownloadType.PANTALLA);
    }
    var documentOp =
        documentTemplates.stream()
            .filter(dt -> dt.itApplies(downloadTypeOp.get()))
            .map(dt -> dt.getDocument(userListMap, state.getCurrentFlow().isAdminFlow()))
            .findFirst();

    resetFlowAndSave(state);
    return documentOp.map(document -> new StateMachineResponse(document, downloadTypeOp.get()))
        .orElseGet(() -> new StateMachineResponse(NO_HAY_FICHAJES, DownloadType.PANTALLA));
  }


  private void incrementTryAndSave(TelegramState state) {
    state.setFlowTries(state.getFlowTries() + 1);
    telegramPersistance.save(state);
  }

  private StateMachineResponse processAkingMonthSubFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    final Predicate<Integer> isMonth = month -> month > 0 && month < 13;
    var monthTry = getIntegerFromMessage(message, isMonth);
    if (monthTry.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_PROCESAR_EL_DATO, DownloadType.PANTALLA);
    }
    state.setFlowMovementMonth(monthTry.get());
    advanceSubFlowAndSave(state);
    return new StateMachineResponse(state.getCurrentSubFlow().getMessage(), DownloadType.PANTALLA);
  }

  private StateMachineResponse processAkingUserIdSubFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    var idTry = Try.of(() -> Long.valueOf(message));
    if (idTry.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_PROCESAR_EL_DATO, DownloadType.PANTALLA);
    }
    state.setFlowTargetUserId(idTry.get());
    advanceSubFlowAndSave(state);
    return new StateMachineResponse(state.getCurrentSubFlow().getMessage(), DownloadType.PANTALLA);
  }

  private StateMachineResponse processAkingYearSubFlow(TelegramState state, String message) {
    if (state.getFlowTries() > MAX_FLOW_TRIES) return triesExceeded(state);
    final Predicate<Integer> isYear = year -> year > 1990 && year < 3000;
    var yearTry = getIntegerFromMessage(message, isYear);
    if (yearTry.isEmpty()) {
      incrementTryAndSave(state);
      return new StateMachineResponse(NO_SE_PUDO_PROCESAR_EL_DATO, DownloadType.PANTALLA);
    }
    state.setFlowMovementYear(yearTry.get());
    advanceSubFlowAndSave(state);
    return new StateMachineResponse(state.getCurrentSubFlow().getMessage(), DownloadType.PANTALLA);
  }

  private void advanceSubFlowAndSave(TelegramState state) {
    state.setCurrentSubFlow(state.getCurrentFlow().getNextStep(state.getCurrentSubFlow()));
    state.setFlowTries(0);
    telegramPersistance.save(state);
  }

  private static Try<Integer> getIntegerFromMessage(String message, Predicate<Integer> isYear) {
    return Try.of(() -> Integer.valueOf(message)).filter(isYear);
  }

  private StateMachineResponse triesExceeded(TelegramState state) {
    resetFlowAndSave(state);
    return new StateMachineResponse(LO_HAS_INTENTADO_DEMASIADAS_VECES_DEBERAS_VOLVER_A_EMPEZAR, DownloadType.PANTALLA);
  }

  private void resetFlowAndSave(TelegramState state) {
    //reset flow
    state.setCurrentFlow(Flow.NO_STATE);
    //reset tries
    state.setFlowTries(0);
    telegramPersistance.save(state);
  }

  public boolean clockIn(TelegramState state) {
    setLastMovementTime(state);
    return movementsService.recordMovement(
        state.getTelegramUserId(),
        state.getTelegramUserId(),
        Type.ENTRADA);
  }

  private void setLastMovementTime(TelegramState state) {
    state.setLastMovement(LocalDateTime.now());
    telegramPersistance.save(state);
  }

  public boolean clockOut(TelegramState state) {
    setLastMovementTime(state);
    return movementsService.recordMovement(
        state.getTelegramUserId(),
        state.getTelegramUserId(),
        Type.SALIDA);
  }


  public Optional<TelegramState> registerNewChat(long chatId) {
    return Try.of(() -> {
      TelegramState saved =
          telegramPersistance.save(
              TelegramState.builder()
                  .currentFlow(Flow.NO_STATE)
                  .chatId(chatId)
                  .lastMovement(LocalDateTime.now().minusMinutes(2))
                  .build()
          );
      return Optional.of(saved);
    }).getOrElse(Optional.empty());
  }

  public List<User> getUsers(TelegramState state) {
    return usersService.getUsers(state.getTelegramUserId());
  }

  public Optional<TelegramState> getState(long chatId) {
    return telegramPersistance.getState(chatId);
  }


  public void setNoState(long chatId) {
    telegramPersistance.getState(chatId)
        .ifPresent(state -> {
          state.setCurrentFlow(Flow.NO_STATE);
          telegramPersistance.save(state);
        });
  }


  private void setAllUsers(TelegramState state, boolean allUsers) {
    state.setIsForAllUsers(allUsers);
    telegramPersistance.save(state);
  }

  private boolean adminBlocking(TelegramState state) {
    return Role.getFromName(state.getUserRole())
        .map(role -> state.getCurrentFlow().isAdminFlow() && (role != Role.ADMIN || role != Role.INSPECTOR))
        .orElse(false);
  }

  public record StateMachineResponse(String message, DownloadType type) {
  }

}
