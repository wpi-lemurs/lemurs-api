/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.roles;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link LemursRoleConverter}. */
class LemursRoleConverterTest {

  private LemursRoleConverter converter;

  @BeforeEach
  void setup() {
    converter = new LemursRoleConverter();
  }

  /** Test converting from a {@link LemursRole} from an integer. */
  @Test
  void testRoleToInt() {
    assertThat(converter.convertToDatabaseColumn(LemursRole.ROLELESS))
        .isEqualTo(LemursRole.ROLELESS.getPermission());
  }

  /** Test converting from an integer to a {@link LemursRole}. */
  @Test
  void testIntToRole() {
    assertThat(converter.convertToEntityAttribute(LemursRole.OWNER.getPermission()))
        .isEqualTo(LemursRole.OWNER);
  }
}
