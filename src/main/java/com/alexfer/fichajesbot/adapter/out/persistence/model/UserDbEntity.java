package com.alexfer.fichajesbot.adapter.out.persistence.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class UserDbEntity {

  @Id
  @GeneratedValue
  private Long id;
  private String name;
  private String personalId;
  private String phone;
  private String activationCode;
  private String role;
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<MovementDbEntity> movements;


}
