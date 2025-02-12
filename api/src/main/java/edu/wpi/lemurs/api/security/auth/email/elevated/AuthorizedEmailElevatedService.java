/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.email.elevated;

import edu.wpi.lemurs.api.exceptions.BadRequestException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.auth.email.AuthorizedEmail;
import edu.wpi.lemurs.api.security.auth.email.elevated.AuthorizedEmailElevated.AuthorizedEmailElevatedKey;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * The {@link AuthorizedEmailElevatedService} is a service that allows for {@link AuthorizedEmail}
 * management.
 */
@Service
@Transactional
public class AuthorizedEmailElevatedService {

  /** Authorized email expiration length in ms. (30 days) */
  private static final long AUTHORIZED_EMAIL_EXPIRATION_TIME = (long) 30 * 24 * 60 * 60 * 1000;

  private SecurityService securityService;
  private AuthorizedEmailElevatedRepository authorizedEmailRepository;

  /** Autowires a {@link AuthorizedEmailElevatedService}. */
  public AuthorizedEmailElevatedService(
      SecurityService securityService,
      AuthorizedEmailElevatedRepository authorizedEmailRepository) {
    this.securityService = securityService;
    this.authorizedEmailRepository = authorizedEmailRepository;
  }

  /**
   * Gets roles if the email is authorized.
   *
   * @param email The user's email.
   * @return The {@link User}.
   */
  public List<LemursRole> getRoles(String email) {
    List<AuthorizedEmailElevated> authorizedEmails = authorizedEmailRepository.findByEmail(email);

    List<LemursRole> roles = new ArrayList<>();
    for (AuthorizedEmailElevated authEmail : authorizedEmails) {
      if (authEmail.getExpiration().before(new Date())) {
        continue;
      }
      roles.add(authEmail.getLemursRole());
    }

    return roles;
  }

  /**
   * Authorizes an email with associated information.
   *
   * @param authorizedEmail An {@link AuthorizedEmail} filled with the relevant information.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.OWNER}
   *     permissions or higher.
   * @throws BadRequestException Thrown if trying to give permissions to {@code LemursRole.USER} or
   *     to a non-existant role.
   */
  public void authorize(String email, LemursRole role)
      throws UnauthenticatedException, UnauthorizedException, BadRequestException {
    if (role.equals(LemursRole.ROLELESS) || role.equals(LemursRole.USER)) {
      throw new BadRequestException();
    }

    // Currently, must have at least the new role to give it. Later maybe change to owner.
    securityService.assertHasPermission(role);

    Date expiration = new Date((new Date()).getTime() + AUTHORIZED_EMAIL_EXPIRATION_TIME);
    AuthorizedEmailElevated authorizedEmail = new AuthorizedEmailElevated(email, role, expiration);

    authorizedEmailRepository.save(authorizedEmail);
  }

  /**
   * Deauthorizes an email.
   *
   * @param email The email to deauthorize.
   * @apiNote This service method does not check permissions.
   */
  public void deauthorizeWithoutAuthCheck(String email, LemursRole role) {
    authorizedEmailRepository.deleteById(new AuthorizedEmailElevatedKey(email, role));
  }
}
