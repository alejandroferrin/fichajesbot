package com.alexfer.fichajesbot.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
  INSPECTOR,
  ADMIN,
  USER;

  public static Optional<Role> getFromName(String name) {
    if (name == null) return Optional.empty();
    return Arrays.stream(Role.values())
        .filter(role -> role.name().startsWith(name))
        .findFirst();
  }
}
