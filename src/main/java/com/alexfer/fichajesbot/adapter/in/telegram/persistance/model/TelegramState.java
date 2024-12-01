package com.alexfer.fichajesbot.adapter.in.telegram.persistance.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class TelegramState {

  @Id
  @GeneratedValue
  private Long id;
  private Long telegramUserId;
  private Long chatId;
  private String userRole;
  private boolean isActivated;
  private int activationTries;
  private int flowTries;
  private Flow currentFlow;
  private SubFlow currentSubFlow;
  private String flowUserName;
  private String flowUserPersonalId;
  private String flowUserPhone;
  private String flowUserRole;
  private Long flowTargetUserId;
  private Long flowMovementId;
  private Integer flowMovementYear;
  private Integer flowMovementMonth;
  private String flowMovementComment;
  private LocalDateTime lastMovement;
  private Boolean isForAllUsers;
}

