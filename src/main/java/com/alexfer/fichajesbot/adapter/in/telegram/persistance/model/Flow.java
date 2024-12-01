package com.alexfer.fichajesbot.adapter.in.telegram.persistance.model;

import lombok.Getter;

import java.util.List;

@Getter
public enum Flow {
  CREATING_USER(List.of(SubFlow.ASKING_ROLE, SubFlow.ASKING_NAME), true),
  DOWNLOAD_OWN_MOVEMENTS(List.of(SubFlow.ASKING_YEAR, SubFlow.ASKING_MONTH, SubFlow.ASKING_DOWNLOAD_TYPE), false),
  DOWNLOAD_ALL_MOVEMENTS(List.of(SubFlow.ASKING_YEAR, SubFlow.ASKING_MONTH, SubFlow.ASKING_DOWNLOAD_TYPE), true),
  DOWNLOAD_USER_MOVEMENTS(List.of(SubFlow.ASKING_USER_ID, SubFlow.ASKING_YEAR, SubFlow.ASKING_MONTH, SubFlow.ASKING_DOWNLOAD_TYPE), true),
  COMMENT_MOVEMENT(List.of(SubFlow.ASKING_MOVEMENT_ID, SubFlow.ASKING_MOV_COMMENT), true),
  RESET_USER(List.of(SubFlow.ASKING_USER_ID), true),
  ACTIVATE_CHAT(List.of(SubFlow.ASKING_DEFAULT_CODE), false),
  NO_STATE(List.of(), false);

  private final List<SubFlow> steps;
  private final boolean isAdminFlow;

  Flow(List<SubFlow> steps, boolean isAdminFlow) {
    this.steps = steps;
    this.isAdminFlow = isAdminFlow;
  }

  public SubFlow getNextStep(SubFlow subFlow) {
    List<SubFlow> stepList = this.getSteps();
    int index = stepList.indexOf(subFlow);
    final int size = stepList.size();
    final int nextItem = index + 1;
    if (nextItem == size) return SubFlow.ENDED;
    return stepList.get(nextItem);
  }

}
