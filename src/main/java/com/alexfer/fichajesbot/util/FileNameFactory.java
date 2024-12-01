package com.alexfer.fichajesbot.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class FileNameFactory {

  public static String create(String name, String extension) {
    LocalDateTime fixedDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
    String formattedDateTime = fixedDateTime.format(formatter);
    return name.toLowerCase() + "_" + formattedDateTime + extension.toLowerCase();
  }

}
