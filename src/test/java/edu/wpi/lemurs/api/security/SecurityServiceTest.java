/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.wpi.lemurs.api.TestConstants;
import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.security.roles.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/** Tests the {@link SecurityService}. */
class SecurityServiceTest implements TestConstants {

  private SecurityContext securityContext;
  private Authentication authentication;
  private RoleService roleService;
  private SecurityService securityService;
  private User user;

  @BeforeEach
  void setup() {
    securityContext = Mockito.mock(SecurityContext.class);
    authentication = Mockito.mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    roleService = mock(RoleService.class);
    securityService = new SecurityService(roleService);

    user = new User(TEST_ID_0, TEST_UMASS_ID_0, false, false);
  }

  /** Tests the security service when there is no authenticated user. */
  @Test
  void testNoUser() {
    assertThatThrownBy(
            () -> {
              securityService.getUser();
            })
        .isInstanceOf(UnauthenticatedException.class);
    when(securityContext.getAuthentication()).thenReturn(null);
    assertThatThrownBy(
            () -> {
              securityService.getUser();
            })
        .isInstanceOf(UnauthenticatedException.class);
  }

  /** Tests the security service when there is an authenticated user. */
  @Test
  void testUser() throws UnauthenticatedException {
    when(authentication.getPrincipal()).thenReturn(user);
    assertThat(securityService.getUser()).isEqualTo(user);
  }

  /** Tests the security service role assertion failing. */
  @Test
  void testDoesNotHaveRole() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(roleService.hasRole(TEST_ID_0, LemursRole.USER)).thenReturn(false);
    assertThatThrownBy(
            () -> {
              securityService.assertHasRole(LemursRole.USER);
            })
        .isInstanceOf(UnauthorizedException.class);
  }

  /** Tests the security service role assertion succeeding. */
  @Test
  void testHasRole() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(roleService.hasRole(TEST_ID_0, LemursRole.USER)).thenReturn(true);
    assertThatCode(
            () -> {
              securityService.assertHasRole(LemursRole.USER);
            })
        .doesNotThrowAnyException();
  }

  /** Tests the security service permission assertion failing. */
  @Test
  void testDoesNotHavePermission() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(roleService.hasPermission(TEST_ID_0, LemursRole.USER)).thenReturn(false);
    assertThatThrownBy(
            () -> {
              securityService.assertHasPermission(LemursRole.USER);
            })
        .isInstanceOf(UnauthorizedException.class);
  }

  /** Tests the security service role assertion succeeding. */
  @Test
  void testHasPermission() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(roleService.hasPermission(TEST_ID_0, LemursRole.USER)).thenReturn(true);
    assertThatCode(
            () -> {
              securityService.assertHasPermission(LemursRole.USER);
            })
        .doesNotThrowAnyException();
  }
}
