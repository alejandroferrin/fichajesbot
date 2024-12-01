package com.alexfer.fichajesbot.adapter.out.persistence;

import com.alexfer.fichajesbot.adapter.out.persistence.model.MovementDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<MovementDbEntity, Long> {

  List<MovementDbEntity> findByYearAndMonth(int year, int month);

  List<MovementDbEntity> findByUserIdAndYearAndMonth(Long userId, int year, int month);
}
