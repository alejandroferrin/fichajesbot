package com.alexfer.fichajesbot.adapter.out.persistence.model;

import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface DbEntityMapper {


  @Mapping(target = "time", source = ".", qualifiedByName = "localDateTimeConverter")
  Movement toMovementDomain(MovementDbEntity entity);

  @Mapping(target = "time", source = "time", qualifiedByName = "ldtTolt")
  @Mapping(target = "year", source = "time", qualifiedByName = "ldtToYear")
  @Mapping(target = "month", source = "time", qualifiedByName = "ldtToMonth")
  @Mapping(target = "day", source = "time", qualifiedByName = "ldtToDay")
  MovementDbEntity toMovementDb(Movement domainObj);

  User toUserDomain(UserDbEntity entity);

  UserDbEntity toUserDb(User domainObj);

  @Named("localDateTimeConverter")
  default LocalDateTime localDateTimeConverter(MovementDbEntity entity) {
    try {
      var day = LocalDate.of(entity.getYear(), entity.getMonth(), entity.getDay());
      return LocalDateTime.of(day, entity.getTime());
    } catch (Exception e) {
      return null;
    }
  }

  @Named("ldtTolt")
  default LocalTime ldtTolt(LocalDateTime ldt) {
    try {
      return LocalTime.of(ldt.getHour(), ldt.getMinute());
    } catch (Exception e) {
      return null;
    }
  }

  @Named("ldtToYear")
  default Integer ldtToYear(LocalDateTime ldt) {
    try {
      return ldt.getYear();
    } catch (Exception e) {
      return null;
    }
  }

  @Named("ldtToMonth")
  default Integer ldtToMonth(LocalDateTime ldt) {
    try {
      return ldt.getMonthValue();
    } catch (Exception e) {
      return null;
    }
  }

  @Named("ldtToDay")
  default Integer ldtToDay(LocalDateTime ldt) {
    try {
      return ldt.getDayOfMonth();
    } catch (Exception e) {
      return null;
    }
  }


//    @Named("encodePass")
//    public String encodePass(String code) {
//        return passwordEncoder.encode(code);
//    }
//
//    @Named("encrypt")
//    public String encrypt(String plainText) {
//        try {
//            return encryptionService.encrypt(plainText);
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    @Named("decrypt")
//    public String decrypt(String plainText) {
//        try {
//            return encryptionService.decrypt(plainText);
//        } catch (Exception e) {
//            return "";
//        }
//    }

}