/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.roles;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Tests the {@link LemursRole}. */
class LemursRoleTest {

  /** Test getting a {@link LemursRole} from its integer representation. */
  @Test
  void testValueOf() {
    assertThat(LemursRole.valueOf(LemursRole.STAFF.getPermission())).isEqualTo(LemursRole.STAFF);
  }

  /** Test getting the authority of a {@link LemursRole}. */
  @Test
  void testGetAuthority() {
    assertThat(LemursRole.RESEARCHER.getAuthority()).isEqualTo("researcher");
  }
}
