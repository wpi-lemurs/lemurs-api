/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.auth.email.AuthorizedEmail;
import edu.wpi.lemurs.api.security.auth.email.AuthorizedEmailService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** The {@link UserService} is a service that allows for {@link User} management. */
@Service
@Transactional
public class UserService {

  private SecurityService securityService;
  private UserRepository userRepository;
  private AuthorizedEmailService authorizedEmailService;

  /** Autowires a {@link UserService}. */
  public UserService(
      SecurityService securityService,
      UserRepository userRepository,
      AuthorizedEmailService authorizedEmailService) {
    this.securityService = securityService;
    this.userRepository = userRepository;
    this.authorizedEmailService = authorizedEmailService;
  }

  /**
   * Gets the user for a given id.
   *
   * @param id The user's id.
   * @return The {@link User}.
   * @throws EntityDoesNotExistException Thrown if there is no user with the given id.
   */
  public User getUser(Integer id) throws EntityDoesNotExistException {
    Optional<User> user = userRepository.findById(id);

    if (user.isEmpty()) {
      throw new EntityDoesNotExistException();
    }

    return user.get();
  }

  /**
   * Creates a {@link User} from a UMass id.
   *
   * @param umassID The new user's umass id.
   * @return The create {@link User}.
   * @throws UnauthorizedException
   * @throws UnauthenticatedException
   */
  public void authroizeEmail(UserDto userDto)
      throws UnauthenticatedException, UnauthorizedException {

    securityService.assertHasPermission(LemursRole.OWNER);

    AuthorizedEmail authorizedEmail =
        new AuthorizedEmail(userDto.getEmail(), userDto.getUmassId(), new Date());
    authorizedEmailService.authorize(authorizedEmail);
  }

  /**
   * Creates a {@link User} from a UMass id. Does not check authorization.
   *
   * @param umassID The new user's umass id.
   * @return The create {@link User}.
   * @throws UnauthorizedException
   * @throws UnauthenticatedException
   */
  public User createUserWithoutAuthorization(Integer umassID) {

    User user = new User(null, umassID, false, false);
    return userRepository.save(user);
  }
}
