/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.email;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
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

  private AuthorizedEmailRepository authorizedEmailRepository;

  /** Autowires a {@link AuthorizedEmailService}. */
  public AuthorizedEmailService(AuthorizedEmailRepository authorizedEmailRepository) {
    this.authorizedEmailRepository = authorizedEmailRepository;
  }

  /**
   * Gets umass id if the email is authorized.
   *
   * @param email The user's email.
   * @return The {@link User}.
   * @throws EntityDoesNotExistException Thrown if there is no user with the given email.
   */
  public Integer getUmassID(String email) throws EntityDoesNotExistException {
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

  public void authorize(AuthorizedEmail authorizedEmail) {
    authorizedEmailRepository.save(authorizedEmail);
  }
}
