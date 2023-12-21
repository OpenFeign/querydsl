package com.querydsl.jpa.domain;

import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class UserRolesConverter implements AttributeConverter<Set<UserRole>, String> {

  @Override
  public String convertToDatabaseColumn(Set<UserRole> roles) {
    if (roles == null || roles.isEmpty()) return null;

    return roles.stream().map(UserRole::name).collect(Collectors.joining(","));
  }

  @Override
  public Set<UserRole> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return Set.of();
    }

    return Arrays.stream(dbData.split(",")).map(UserRole::valueOf).collect(Collectors.toSet());
  }
}
