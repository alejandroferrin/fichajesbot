package com.alexfer.fichajesbot.adapter.in.telegram;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.TelegramPersistanceAdapter;
import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.Flow;
import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.SubFlow;
import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.TelegramState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FlowsStarter {

  private final TelegramPersistanceAdapter telegramPersistance;


  public String startOwnSummary(TelegramState state) {
    final Flow flow = Flow.DOWNLOAD_OWN_MOVEMENTS;
    return getNextFlowMessage(state, flow);
  }

  public String startUserSummary(TelegramState state) {
    final Flow flow = Flow.DOWNLOAD_USER_MOVEMENTS;
    return getNextFlowMessage(state, flow);
  }


  public String startChatActivation(TelegramState state) {
    final Flow flow = Flow.ACTIVATE_CHAT;
    return getNextFlowMessage(state, flow);
  }


  public String startAlta(TelegramState state) {
    final Flow flow = Flow.CREATING_USER;
    return getNextFlowMessage(state, flow);
  }

  public String startAllSummary(TelegramState state) {
    final Flow flow = Flow.DOWNLOAD_ALL_MOVEMENTS;
    return getNextFlowMessage(state, flow);
  }

  public String startResetUser(TelegramState state) {
    final Flow flow = Flow.RESET_USER;
    return getNextFlowMessage(state, flow);
  }

  public String startCommentMovement(TelegramState state) {
    final Flow flow = Flow.COMMENT_MOVEMENT;
    return getNextFlowMessage(state, flow);
  }

  private String getNextFlowMessage(TelegramState state, Flow flow) {
    state.setCurrentFlow(flow);
    state.setCurrentSubFlow(flow.getNextStep(SubFlow.STARTING));
    telegramPersistance.save(state);
    return state.getCurrentSubFlow().getMessage();
  }

}
