package edu.wpi.lemurs.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.security.roles.RoleService;

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
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (User.class.isAssignableFrom(principal.getClass())) {
      return (User) principal;
    } else {
      throw new UnauthenticatedException();
    }
  }

  public void assertHasRole(LemursRole role) throws UnauthenticatedException, UnauthorizedException {
    if (!roleService.hasRole(getUser().getId(), role)) {
      throw new UnauthorizedException();
    }
  }

  public void assertHasPermission(LemursRole role) throws UnauthenticatedException, UnauthorizedException {
    if (!roleService.hasPermission(getUser().getId(), role)) {
      throw new UnauthorizedException();
    }
  }
}

