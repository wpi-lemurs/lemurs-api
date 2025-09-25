/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.distance;

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
public class DistanceService {

  private final SecurityService securityService;
  private final DistanceRepository distanceRepository;

  @Autowired
  public DistanceService(DistanceRepository distanceRepository, SecurityService securityService) {
    this.distanceRepository = distanceRepository;
    this.securityService = securityService;
  }

  /**
   * Records step data for a user.
   *
   * @param distanceDto The {@link DistanceDto}.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public void recordDistance(DistanceDto distanceDto)
      throws UnauthenticatedException, UnauthorizedException {

    securityService.assertHasRole(LemursRole.USER);

    Distance distance =
        new Distance(
            null,
            securityService.getUser().getId(),
            distanceDto.getDistance(),
            distanceDto.getStart_timestamp(),
            distanceDto.getEnd_timestamp(),
            distanceDto.getAppSource(),
            new Date(),
            distanceDto.getType() != null ? distanceDto.getType() : "distance",
            "{}",
            DataStatus.NOT_PROCESSED);

    distanceRepository.save(distance);
  }
}
