/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.speed;

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
public class SpeedService {

  private final SecurityService securityService;
  private final SpeedRepository speedRepository;

  @Autowired
  public SpeedService(SpeedRepository speedRepository, SecurityService securityService) {
    this.speedRepository = speedRepository;
    this.securityService = securityService;
  }

  /**
   * Records speed data for a user.
   *
   * @param speedDto The {@link SpeedDto}.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public void recordSpeed(SpeedDto speedDto)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Speed speed =
        new Speed(
            null,
            securityService.getUser().getId(),
            speedDto.getSpeed(),
            speedDto.getStart_timestamp(),
            speedDto.getEnd_timestamp(),
            speedDto.getUnit() != null ? speedDto.getUnit() : "m/s",
            speedDto.getAppSource(),
            new Date(),
            speedDto.getType() != null ? speedDto.getType() : "speed",
            "{}",
            DataStatus.NOT_PROCESSED);

    speedRepository.save(speed);
  }
}
