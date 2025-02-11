/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.roles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.wpi.lemurs.api.TestConstants;
import edu.wpi.lemurs.api.endpoints.user.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Tests the {@link RoleService}. */
class RoleServiceTest implements TestConstants {

  private RoleRepository roleRepository;
  private RoleService roleService;
  private User user;
  private ArrayList<LemursRole> lemursRoles;
  private ArrayList<Role> roles;

  @BeforeEach
  void setup() {
    roleRepository = mock(RoleRepository.class);
    roleService = new RoleService(roleRepository);
    user = new User(TEST_ID_0, false, false);
    lemursRoles = new ArrayList<>();
    roles = new ArrayList<>();
    when(roleRepository.findByUserId(TEST_ID_0)).thenReturn(roles);
  }

  /** Prepares the roles array based on the given user and lemursRoles array. */
  private void prepareRoles(Integer userID, List<LemursRole> lemursRoles) {
    roles.clear();
    for (LemursRole role : lemursRoles) {
      roles.add(new Role(userID, role));
    }
  }

  /** Tests user with no roles. */
  @Test
  void testNoRoles() {
    prepareRoles(TEST_ID_0, lemursRoles);
    assertThat(roleService.getRoles(user.getId())).isEmpty();
  }

  /** Tests user with multiple roles. */
  @Test
  void testMultipleRoles() {
    lemursRoles.add(LemursRole.USER);
    lemursRoles.add(LemursRole.RESEARCHER);
    prepareRoles(TEST_ID_0, lemursRoles);
    assertThat(roleService.getRoles(user.getId()))
        .containsExactly(LemursRole.USER, LemursRole.RESEARCHER);
  }

  /** Test checking if user doesn't have a role. */
  @Test
  void testDoesNotHaveRole() {
    lemursRoles.add(LemursRole.RESEARCHER);
    prepareRoles(TEST_ID_0, lemursRoles);
    assertThat(roleService.hasRole(TEST_ID_0, LemursRole.USER)).isFalse();
  }

  /** Test checking if user has a role. */
  @Test
  void testHasRole() {
    lemursRoles.add(LemursRole.RESEARCHER);
    lemursRoles.add(LemursRole.USER);
    prepareRoles(TEST_ID_0, lemursRoles);
    assertThat(roleService.hasRole(TEST_ID_0, LemursRole.USER)).isTrue();
  }

  /** Test checking if user doesn't have a permission. */
  @Test
  void testDoesNotHavePermission() {
    lemursRoles.add(LemursRole.USER);
    prepareRoles(TEST_ID_0, lemursRoles);
    assertThat(roleService.hasPermission(TEST_ID_0, LemursRole.RESEARCHER)).isFalse();
  }

  /** Test checking if user has permission. */
  @Test
  void testHasPermission() {
    lemursRoles.add(LemursRole.RESEARCHER);
    prepareRoles(TEST_ID_0, lemursRoles);
    assertThat(roleService.hasPermission(TEST_ID_0, LemursRole.USER)).isTrue();
  }

  /** Test adding a role. */
  @Test
  void testAddingRole() {
    ArgumentCaptor<Role> receivedRole = ArgumentCaptor.forClass(Role.class);
    roleService.addRoleWithoutAuthCheck(TEST_ID_0, LemursRole.USER);
    verify(roleRepository).save(receivedRole.capture());

    Role role = receivedRole.getValue();
    assertThat(role.getUserId()).isEqualTo(TEST_ID_0);
    assertThat(role.getLemursRole()).isEqualTo(LemursRole.USER);
  }
}
