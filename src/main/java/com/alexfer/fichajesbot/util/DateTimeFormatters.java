package com.alexfer.fichajesbot.util;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeFormatters {

  private static final String YYYY_MM_DD = "yyyy-MM-dd";
  private static final String HH_MM = "HH:mm";
  private static final String SPACE = " ";
  public static final String DD_MM_YYYY = "dd-MM-yyyy";


  public static DateTimeFormatter getDateFormatter() {
    return DateTimeFormatter.ofPattern(YYYY_MM_DD);
  }

  public static DateTimeFormatter getDateTimeFormatterCsv(String separator) {
    return DateTimeFormatter.ofPattern(YYYY_MM_DD + separator + HH_MM);
  }

  public static DateTimeFormatter getDateTimeFormatterESP() {
    return DateTimeFormatter.ofPattern(DD_MM_YYYY + SPACE + HH_MM);
  }


}
