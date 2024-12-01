package com.alexfer.fichajesbot.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class Movement {

  private Long id;
  private LocalDateTime time;
  private String comment;
  private Type type;
  private User user;

}
