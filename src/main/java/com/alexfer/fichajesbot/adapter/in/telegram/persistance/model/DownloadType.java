package com.alexfer.fichajesbot.adapter.in.telegram.persistance.model;

import java.util.Arrays;
import java.util.Optional;

public enum DownloadType {
  NONE,
  PANTALLA,
  DOCUMENTO;

  public static Optional<DownloadType> getFromName(String name) {
    if (name == null) return Optional.empty();
    return Arrays.stream(DownloadType.values())
        .filter(dt -> dt.name().startsWith(name))
        .findFirst();
  }


}
