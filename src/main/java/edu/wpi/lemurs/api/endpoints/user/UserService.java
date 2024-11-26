/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import java.util.Optional;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

/** The {@link UserService} is a service that allows for {@link User} management. */
@Service
public class UserService {

  private UserRepository userRepository;

  /** Autowires a {@link UserService}. */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Gets the user for a given id.
   *
   * @param id The user's id.
   * @return The {@link User}.
   * @throws EntityDoesNotExistException Thrown if there is no user with the given id.
   * @apiNote This does not check for authorization.
   */
  public User getUserWithoutAuthCheck(Integer id) throws EntityDoesNotExistException {
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
   * @apiNote This user service does not check for authorization.
   */
  public User createUserWithoutAuthCheck(String umassID) {
    User user = new User(null, umassID, false, false);
    return userRepository.save(user);
  }

  /**
   * Throws an {@link DisabledException} if the user is disabled or deleted.
   *
   * @param user The user to check.
   * @throws DisabledException Thrown if the account is disabled or deleted.
   */
  public void assertEnabledUser(User user) throws DisabledException {
    if (user.isDisabled()) {
      throw new DisabledException("Account is disabled.");
    }

    if (user.isDeleted()) {
      throw new DisabledException("Account is deleted.");
    }
  }
}
