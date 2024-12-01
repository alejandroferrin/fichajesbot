package com.alexfer.fichajesbot.adapter.out.persistence;

import com.alexfer.fichajesbot.adapter.out.persistence.model.UserDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDbEntity, Long> {

  Optional<UserDbEntity> findByActivationCode(String activationCode);

}
