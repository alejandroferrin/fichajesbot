package com.alexfer.fichajesbot.adapter.in.telegram.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

@UtilityClass
public class Commands {


  public final String START = "/start";
  public final String MENU = "/menu";


  public boolean isStartCommand(Update update) {
    return Objects.equals(START, update.getMessage().getText());
  }

  public boolean isMenuCommand(Update update) {
    return Objects.equals(MENU, update.getMessage().getText());
  }

}
