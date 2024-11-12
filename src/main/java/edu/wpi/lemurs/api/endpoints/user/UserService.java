/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** The {@link UserService} is a service that allows for {@link User} management. */
@Service
@Transactional
public class UserService {

  private SecurityService securityService;
  private UserRepository userRepository;

  /** Autowires a {@link UserService}. */
  public UserService(SecurityService securityService, UserRepository userRepository) {
    this.securityService = securityService;
    this.userRepository = userRepository;
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
   * @param userDto The new user's info.
   * @return The create {@link User}.
   * @throws UnauthorizedException 
   * @throws UnauthenticatedException 
   */
  public User createUser(UserDto userDto) throws UnauthenticatedException, UnauthorizedException {

    securityService.assertHasPermission(LemursRole.OWNER);

    User user = new User(null, userDto.getUmassId(), false, false);
    return userRepository.save(user);
  }
}
