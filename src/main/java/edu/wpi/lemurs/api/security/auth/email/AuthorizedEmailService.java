/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.email;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * The {@link AuthorizedEmailService} is a service that allows for {@link AuthorizedEmail}
 * management.
 */
@Service
@Transactional
public class AuthorizedEmailService {

  /** Authorized email expiration length in ms. (7 days) */
  private static final long AUTHORIZED_EMAIL_EXPIRATION_TIME = (long) 7 * 24 * 60 * 60 * 1000;

  private SecurityService securityService;
  private AuthorizedEmailRepository authorizedEmailRepository;

  /** Autowires a {@link AuthorizedEmailService}. */
  public AuthorizedEmailService(
      SecurityService securityService, AuthorizedEmailRepository authorizedEmailRepository) {
    this.securityService = securityService;
    this.authorizedEmailRepository = authorizedEmailRepository;
  }

  /**
   * Gets umass id if the email is authorized.
   *
   * @param email The user's email.
   * @return The {@link User}.
   * @throws EntityDoesNotExistException Thrown if there is no user with the given email.
   */
  public String getUmassID(String email) throws EntityDoesNotExistException {
    Optional<AuthorizedEmail> authorizedEmail = authorizedEmailRepository.findById(email);

    if (authorizedEmail.isEmpty()) {
      throw new EntityDoesNotExistException();
    }

    AuthorizedEmail toCheck = authorizedEmail.get();

    if (toCheck.getExpiration().before(new Date())) {
      throw new EntityDoesNotExistException();
    }

    return toCheck.getUmassId();
  }

  /**
   * Authorizes an email with associated information.
   *
   * @param authorizedEmail An {@link AuthorizedEmail} filled with the relevant information.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.OWNER}
   *     permissions or higher.
   */
  public void authorize(String email, String umassID)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.OWNER);

    Date expiration = new Date((new Date()).getTime() + AUTHORIZED_EMAIL_EXPIRATION_TIME);
    AuthorizedEmail authorizedEmail = new AuthorizedEmail(email, umassID, expiration);

    authorizedEmailRepository.save(authorizedEmail);
  }

  /**
   * Deauthorizes an email.
   *
   * @param email The email to deauthorize.
   * @apiNote This service method does not check permissions.
   */
  public void deauthorizeWithoutAuthCheck(String email) {
    authorizedEmailRepository.deleteById(email);
  }
}
