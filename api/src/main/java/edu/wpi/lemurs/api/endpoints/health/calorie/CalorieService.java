/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.calorie;

import edu.wpi.lemurs.api.endpoints.data.DataStatus;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CalorieService {
  private final SecurityService securityService;
  private final CalorieRepository calorieRepository;

  @Autowired
  public CalorieService(CalorieRepository calorieRepository, SecurityService securityService) {
    this.calorieRepository = calorieRepository;
    this.securityService = securityService;
  }

  /**
   * Records calorie data for a user.
   *
   * @param calorieDto The {@link CalorieDto}.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public void recordCalories(CalorieDto calorieDto)
      throws UnauthenticatedException, UnauthorizedException {

    securityService.assertHasRole(LemursRole.USER);

    Calorie calorie =
        new Calorie(
            null,
            securityService.getUser().getId(),
            calorieDto.getCalories(),
            calorieDto.getStart_timestamp(),
            calorieDto.getEnd_timestamp(),
            calorieDto.getAppSource(),
            new Date(),
            calorieDto.getType() != null ? calorieDto.getType() : "calories",
            "{}",
            DataStatus.NOT_PROCESSED);

    calorieRepository.save(calorie);
  }
}
