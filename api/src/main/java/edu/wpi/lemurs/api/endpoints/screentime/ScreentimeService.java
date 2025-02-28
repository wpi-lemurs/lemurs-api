/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.screentime;

import edu.wpi.lemurs.api.endpoints.progress.ProgressService;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ScreentimeService {

  private SecurityService securityService;
  private ScreentimeRepository screentimeRepository;
  private ScreentimeAppRepository screentimeAppRepository;
  private ProgressService progressService;

  @Autowired
  public ScreentimeService(
      SecurityService securityService,
      ScreentimeRepository screentimeRepository,
      ScreentimeAppRepository screentimeAppRepository,
      ProgressService progressService) {
    this.securityService = securityService;
    this.screentimeRepository = screentimeRepository;
    this.screentimeAppRepository = screentimeAppRepository;
    this.progressService = progressService;
  }

  /**
   * Records screentime data for a user.
   *
   * @param screentimeDto The {@link ScreentimeDto}.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public void recordScreentime(ScreentimeDto screentimeDto)
      throws UnauthenticatedException, UnauthorizedException {

    securityService.assertHasRole(LemursRole.USER);

    Screentime screentime =
        new Screentime(
            null,
            securityService.getUser().getId(),
            new Date(),
            screentimeDto.getStartTime(),
            screentimeDto.getEndTime());
    screentime = screentimeRepository.save(screentime);

    List<ScreentimeApp> screentimeApps = new ArrayList<>();
    for (ScreentimeAppDto screentimeAppDto : screentimeDto.getUsageData()) {
      screentimeApps.add(
          new ScreentimeApp(
              null,
              screentime.getId(),
              screentimeAppDto.getAppName(),
              screentimeAppDto.getTotalTime(),
              screentimeAppDto.getLastTimeUsed()));
    }
    screentimeAppRepository.saveAll(screentimeApps);
  }
}
