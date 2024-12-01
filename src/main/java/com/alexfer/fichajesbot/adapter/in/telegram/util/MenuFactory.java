package com.alexfer.fichajesbot.adapter.in.telegram.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MenuFactory {


  public static final String ENTRAR_CALLBACK = "ENTRAR_CALLBACK";
  public static final String SALIR_CALLBACK = "SALIR_CALLBACK";
  public static final String RESUMEN_CALLBACK = "RESUMEN_CALLBACK";
  public static final String ALTA_CALLBACK = "ALTA_CALLBACK";
  public static final String USUARIOS_CALLBACK = "USUARIOS_CALLBACK";
  public static final String RESUMEN_USUARIO_CALLBACK = "RESUMEN_USUARIO_CALLBACK";
  public static final String RESUMEN_TODOS_CALLBACK = "RESUMEN_TODOS_CALLBACK";
  public static final String COMENTAR_FICHAJE_CALLBACK = "COMENTAR_FICHAJE_CALLBACK";
  public static final String RESTABLECER_USUARIO_CALLBACK = "RESTABLECER_USUARIO_CALLBACK";

  public static SendMessage getUserMainMenu(long chatId) {
    SendMessage menu = new SendMessage();
    menu.setChatId(chatId);
    menu.setText("*Acciones Usuario*");
    InlineKeyboardMarkup languagesMenu = getUserMainMenu();
    menu.setReplyMarkup(languagesMenu);
    return menu;
  }

  public static SendMessage getAdminMainMenu(long chatId) {
    SendMessage menu = new SendMessage();
    menu.setChatId(chatId);
    menu.setText("*Acciones Admin*");
    InlineKeyboardMarkup languagesMenu = getAdminMainMenu();
    menu.setReplyMarkup(languagesMenu);
    return menu;
  }

  public static SendMessage getInspectorMainMenu(long chatId) {
    SendMessage menu = new SendMessage();
    menu.setChatId(chatId);
    menu.setText("*Acciones Inspector*");
    InlineKeyboardMarkup languagesMenu = getInspectorMainMenu();
    menu.setReplyMarkup(languagesMenu);
    return menu;
  }

  private static InlineKeyboardMarkup getInspectorMainMenu() {
    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    List<InlineKeyboardButton> row1 = new ArrayList<>();
    row1.add(getButton("Usuarios \uD83D\uDC65", USUARIOS_CALLBACK));
    List<InlineKeyboardButton> row2 = new ArrayList<>();
    row2.add(getButton("Resumen usuario \uD83D\uDCCA", RESUMEN_USUARIO_CALLBACK));
    row2.add(getButton("Resumen todos \uD83D\uDCCA", RESUMEN_TODOS_CALLBACK));
    rowsInline.add(row1);
    rowsInline.add(row2);
    markupInline.setKeyboard(rowsInline);
    return markupInline;
  }

  private static InlineKeyboardMarkup getAdminMainMenu() {
    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    List<InlineKeyboardButton> row1 = new ArrayList<>();
    row1.add(getButton("Alta \uD83C\uDD95", ALTA_CALLBACK));
    row1.add(getButton("Usuarios \uD83D\uDC65", USUARIOS_CALLBACK));
    List<InlineKeyboardButton> row2 = new ArrayList<>();
    row2.add(getButton("Resumen usuario \uD83D\uDCCA", RESUMEN_USUARIO_CALLBACK));
    row2.add(getButton("Resumen todos \uD83D\uDCCA", RESUMEN_TODOS_CALLBACK));
    List<InlineKeyboardButton> row3 = new ArrayList<>();
    row3.add(getButton("Comentar fichaje ✏️", COMENTAR_FICHAJE_CALLBACK));
    List<InlineKeyboardButton> row4 = new ArrayList<>();
    row4.add(getButton("Restablecer usuario \uD83D\uDD04", RESTABLECER_USUARIO_CALLBACK));
    rowsInline.add(row1);
    rowsInline.add(row2);
    rowsInline.add(row3);
    rowsInline.add(row4);
    markupInline.setKeyboard(rowsInline);
    return markupInline;
  }

  private static InlineKeyboardMarkup getUserMainMenu() {
    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    List<InlineKeyboardButton> row1 = new ArrayList<>();
    row1.add(getButton("Entrar \uD83D\uDFE2➡️", ENTRAR_CALLBACK));
    row1.add(getButton("Salir \uD83D\uDD34⬅️", SALIR_CALLBACK));
    List<InlineKeyboardButton> row2 = new ArrayList<>();
    row2.add(getButton("Resumen \uD83D\uDCCA", RESUMEN_CALLBACK));
    rowsInline.add(row1);
    rowsInline.add(row2);
    markupInline.setKeyboard(rowsInline);
    return markupInline;
  }

  private static InlineKeyboardButton getButton(String text, String callback) {
    InlineKeyboardButton inlineButtonLanguage = new InlineKeyboardButton();
    inlineButtonLanguage.setText(text);
    inlineButtonLanguage.setCallbackData(callback);
    return inlineButtonLanguage;
  }


}
