/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.security.roles.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/** The {@link SecurityService} provides information on the authenticated {@link User}. */
@Service
public class SecurityService {

  private RoleService roleService;

  /** Autowires the {@link SecurityService}. */
  @Autowired
  public SecurityService(RoleService roleService) {
    this.roleService = roleService;
  }

  /**
   * Gets the authenticated {@link User}.
   *
   * @return The authenticated {@link User}.
   * @throws UnauthenticatedException Thrown when no {@link User} is authenticated.
   */
  public User getUser() throws UnauthenticatedException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new UnauthenticatedException();
    }
    Object principal = authentication.getPrincipal();
    if (principal == null) {
      throw new UnauthenticatedException();
    }

    if (User.class.isAssignableFrom(principal.getClass())) {
      return (User) principal;
    } else {
      throw new UnauthenticatedException();
    }
  }

  /**
   * Throws an exception if the user does not have the given role.
   *
   * @param role The role to check for.
   * @throws UnauthenticatedException Thrown if the user does not have the role.
   * @throws UnauthorizedException Thrown if the user is not authenticated.
   */
  public void assertHasRole(LemursRole role)
      throws UnauthenticatedException, UnauthorizedException {
    if (!roleService.hasRole(getUser().getId(), role)) {
      throw new UnauthorizedException();
    }
  }

  /**
   * Throws an exception if the user does not have the given permission level.
   *
   * @param role The role to compare against.
   * @throws UnauthenticatedException Thrown if the user does not have the permission level of the
   *     given role or higher.
   * @throws UnauthorizedException Thrown if the user is not authenticated.
   */
  public void assertHasPermission(LemursRole role)
      throws UnauthenticatedException, UnauthorizedException {
    if (!roleService.hasPermission(getUser().getId(), role)) {
      throw new UnauthorizedException();
    }
  }
}
