/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.step;

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
public class StepService {

  private final SecurityService securityService;
  private final StepRepository stepRepository;

  @Autowired
  public StepService(StepRepository stepRepository, SecurityService securityService) {
    this.stepRepository = stepRepository;
    this.securityService = securityService;
  }

  /**
   * Records step data for a user.
   *
   * @param stepDto The {@link StepDto}.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public void recordSteps(StepDto stepDto) throws UnauthenticatedException, UnauthorizedException {

    securityService.assertHasRole(LemursRole.USER);

    Step step =
        new Step(
            null,
            securityService.getUser().getId(),
            stepDto.getSteps(),
            stepDto.getStartTimestamp(),
            stepDto.getEndTimestamp(),
            stepDto.getAppSource(),
            new Date(),
            stepDto.getType() != null ? stepDto.getType() : "steps",
            "{}",
            DataStatus.NOT_PROCESSED);

    stepRepository.save(step);
  }
}
