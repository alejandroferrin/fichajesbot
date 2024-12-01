package com.alexfer.fichajesbot.application.in.util;

import java.util.Random;

public class ActivationCodeGenerator {

  public static int generate() {
    Random random = new Random();
    // El rango 100000 (inclusive) a 1000000 (exclusivo) asegura que el número tenga 6 cifras
    return 100000 + random.nextInt(900000);
  }

}


