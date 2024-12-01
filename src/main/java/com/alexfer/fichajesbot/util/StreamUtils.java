package com.alexfer.fichajesbot.util;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class StreamUtils {

  public static InputStream getInputStreamFromString(String msg) {
    byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
    return new ByteArrayInputStream(bytes);
  }

}
