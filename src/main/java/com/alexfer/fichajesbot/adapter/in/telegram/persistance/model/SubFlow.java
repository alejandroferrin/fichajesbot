package com.alexfer.fichajesbot.adapter.in.telegram.persistance.model;

import lombok.Getter;

@Getter
public enum SubFlow {
  STARTING("..."),
  ENDED("..."),
  ASKING_USER_ID("Ingresa el id del usuario"),
  ASKING_YEAR("Envía el año. Ejemplo: 2025"),
  ASKING_MONTH("Envía el mes. Ejemplo: 6"),
  ASKING_DOWNLOAD_TYPE("Elige: pantalla o documento"),
  ASKING_ROLE("Elige el rol: admin, user o inspector"),
  ASKING_NAME("Manda el nombre."),
  ASKING_PERSONAL_ID("Manda si quieres un número de documento identificativo o n/a"),
  ASKING_PHONE("Manda el teléfono"),
  ASKING_MOVEMENT_ID("Ingresa el id del movimiento"),
  ASKING_MOV_COMMENT("Ingresa el comentario"),
  ASKING_DEFAULT_CODE("Envía tu código de activación");

  private final String message;


  SubFlow(String message) {

    this.message = message;
  }
}
