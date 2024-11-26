/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.roles;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * A {@link LemursRoleConverter} converts between the {@link LemursRole} enum and an integer
 * permission representation.
 */
@Converter()
public class LemursRoleConverter implements AttributeConverter<LemursRole, Integer> {
  @Override
  public Integer convertToDatabaseColumn(LemursRole role) {
    return (role != null) ? role.getPermission() : -1;
  }

  @Override
  public LemursRole convertToEntityAttribute(Integer permission) {
    return LemursRole.valueOf(permission);
  }
}
