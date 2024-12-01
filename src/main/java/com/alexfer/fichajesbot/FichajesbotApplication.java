package com.alexfer.fichajesbot;

import com.alexfer.fichajesbot.application.out.SecuredPersistancePort;
import com.alexfer.fichajesbot.domain.Role;
import com.alexfer.fichajesbot.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Slf4j
@ComponentScan(basePackages = {
    "com.alexfer.fichajesbot",
    "org.telegram.telegrambots"
})
public class FichajesbotApplication implements CommandLineRunner {

  @Autowired
  SecuredPersistancePort persistancePort;

  @Value("${admin.default.activation.code}")
  private String code;

  @Value("${admin.default.name}")
  private String name;

  public static void main(String[] args) {
    SpringApplication.run(FichajesbotApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    var users = persistancePort.findAllUsers().stream().findAny();

    if (users.isEmpty()) {
      saveUser(name);
    }

  }

  private void saveUser(String admin) {
    persistancePort.saveUser(
        User.builder()
            .name(admin)
            .personalId("")
            .phone("n/a")
            .role(Role.ADMIN)
            .activationCode(code)
            .build()
    );
  }


}
