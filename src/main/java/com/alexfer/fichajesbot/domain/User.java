package com.alexfer.fichajesbot.domain;

import lombok.Builder;
import lombok.Data;

import java.io.StringWriter;

@Builder
@Data
public class User {

  private Long id;
  private String name;
  private String personalId;
  private String phone;
  private Role role;
  private String activationCode;
//  private List<Movement> movements;

  public String toString() {
    return new StringWriter()
        .append("Id: ")
        .append(String.valueOf(id))
        .append(", ")
        .append("Nombre: ")
        .append(name)
        .append(", ")
        .append("Rol: ")
        .append(role.name())
        .append(".")
        .toString();
  }

}
