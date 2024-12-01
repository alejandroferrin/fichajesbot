package com.alexfer.fichajesbot.adapter.out.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class MovementDbEntity {

  @Id
  @GeneratedValue
  private Long id;
  @Column(name = "movement_time")
  private LocalTime time;
  @Column(name = "movement_year")
  private Integer year;
  @Column(name = "movement_month")
  private Integer month;
  @Column(name = "movement_day")
  private Integer day;
  private String comment;
  private String type;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private UserDbEntity user;
}
