/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.umass;

import java.util.Optional;
import org.springframework.stereotype.Service;

/** The {@link UmassService} is a service that allows for {@link Umass} management. */
@Service
public class UmassService {

  private UmassRepository umassRepository;

  /** Autowires a {@link UmassService}. */
  public UmassService(UmassRepository umassRepository) {
    this.umassRepository = umassRepository;
  }

  /**
   * Saves a user with a umass id.
   *
   * @param userID The user id to assign the umass id to.
   * @param umassID The umass id to assign.
   * @apiNote This does not check for authorization.
   */
  public void setUmassIDWithoutAuthorization(Integer userID, String umassID) {
    // TODO: Check for and solve issue where 2 users are given the same id.
    umassRepository.save(new Umass(userID, umassID));
  }

  /**
   * Gets the umass id for a user id.
   *
   * @param userID The user id.
   * @return The umass id for the user id. (Empty optional if nothing found.)
   * @apiNote This does not check for authorization.
   */
  public Optional<String> getUmassID(Integer userID) {
    Optional<Umass> umass = umassRepository.findById(userID);
    if (umass.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(umass.get().getUmassId());
    }
  }
}
