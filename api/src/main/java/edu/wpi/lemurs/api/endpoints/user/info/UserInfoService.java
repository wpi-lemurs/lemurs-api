/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.info;

import java.util.Optional;
import org.springframework.stereotype.Service;

/** The {@link UserInfoService} is a service that allows for {@link UserInfo} management. */
@Service
public class UserInfoService {

  private UserInfoRepository userInfoRepository;

  /** Autowires a {@link UserInfoService}. */
  public UserInfoService(UserInfoRepository userInfoRepository) {
    this.userInfoRepository = userInfoRepository;
  }

  /**
   * Saves a user with their info.
   *
   * @param userID The user id to assign the info to.
   * @param email The email of the user.
   * @param firstName The first name of the user.
   * @param lastName The last name of the user.
   * @apiNote This does not check for authorization.
   */
  public void setUserInfoWithoutAuthorization(
      Integer userID, String email, String firstName, String lastName) {
    userInfoRepository.save(new UserInfo(userID, email, firstName, lastName));
  }

  /**
   * Gets the user info for a user id.
   *
   * @param userID The user id.
   * @return The user info for the user id. (Empty optional if nothing found.)
   * @apiNote This does not check for authorization.
   */
  public Optional<UserInfo> getUserInfo(Integer userID) {
    return userInfoRepository.findById(userID);
  }

  /**
   * Checks whether there is already info associated with an id.
   *
   * @param userID The user id to check.
   * @return Whether there is info associated with the id.
   */
  public boolean doesIDExist(Integer userID) {
    return userInfoRepository.existsById(userID);
  }
}
