package com.alexfer.fichajesbot.application.in.secured;

import com.alexfer.fichajesbot.domain.User;

import java.util.List;
import java.util.Optional;

public interface SecuredUsersPort {

  Optional<User> activateUser(String activationCode);

  Optional<String> createUser(Long requesterId, User user);

  Optional<String> regenerateCode(Long requesterId, Long userId);

  List<User> getUsers(Long requesterId);

  Optional<User> getUser(Long requesterId, Long targetId);
}
