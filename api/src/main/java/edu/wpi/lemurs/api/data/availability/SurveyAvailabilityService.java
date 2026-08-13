/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.data.availability;

import edu.wpi.lemurs.api.endpoints.progress.Progress;
import edu.wpi.lemurs.api.endpoints.progress.ProgressRepository;
import edu.wpi.lemurs.api.endpoints.survey.SurveyStatusResponse;
import edu.wpi.lemurs.api.endpoints.survey.SurveyWindowDto;
import edu.wpi.lemurs.api.endpoints.survey.answer.SurveyResponse;
import edu.wpi.lemurs.api.endpoints.survey.answer.SurveyResponseRepository;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The {@link SurveyAvailabilityService} is a service that allows for {@link Data} management. */
@Service
@Transactional
public class SurveyAvailabilityService {

  private SecurityService securityService;
  private SurveyAvailabilityRepository surveyAvailabilityRepository;
  private SurveyResponseRepository surveyResponseRepository;
  private ProgressRepository progressRepository;
  private SurveyWindowProperties surveyWindowProperties;

  /** Autowires a {@link SurveyAvailabilityService}. */
  @Autowired
  public SurveyAvailabilityService(
      SecurityService securityService,
      SurveyAvailabilityRepository surveyAvailabilityRepository,
      SurveyResponseRepository surveyResponseRepository,
      ProgressRepository progressRepository,
      SurveyWindowProperties surveyWindowProperties) {
    this.securityService = securityService;
    this.surveyAvailabilityRepository = surveyAvailabilityRepository;
    this.surveyResponseRepository = surveyResponseRepository;
    this.progressRepository = progressRepository;
    this.surveyWindowProperties = surveyWindowProperties;
  }

  /**
   * Reports the survey windows and what the participant has already completed on their own local
   * date.
   *
   * <p>Nothing in this method depends on the server's timezone. It never asks what time it is here;
   * it only answers what the windows are and what this participant has already submitted during the
   * local day they named. Deciding whether a window is currently open is the client's job, because
   * only the client knows where the participant is.
   *
   * @param clientLocalDate The participant's own calendar date.
   * @param clientZone The participant's own timezone.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public SurveyStatusResponse getStatus(LocalDate clientLocalDate, ZoneId clientZone)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.USER);
    Integer userId = securityService.getUser().getId();

    List<SurveyWindowDto> windows = new ArrayList<>();
    for (SurveyAvailability availability : surveyAvailabilityRepository.findAll()) {
      windows.add(
          new SurveyWindowDto(
              availability.getName(),
              availability.getOpenTime().toLocalTime(),
              availability.getCloseTime().toLocalTime(),
              surveyWindowProperties.getWindowSurveyIds().get(availability.getName())));
    }

    // The exact instants at which the participant's local day begins and ends.
    // atStartOfDay(zone) resolves correctly on days where local midnight does
    // not exist because of a daylight saving transition.
    Date dayStart = Date.from(clientLocalDate.atStartOfDay(clientZone).toInstant());
    Date dayEnd = Date.from(clientLocalDate.plusDays(1).atStartOfDay(clientZone).toInstant());

    Set<Integer> completedSurveyIds = new HashSet<>();
    for (SurveyResponse response :
        surveyResponseRepository.findByUserBetween(userId, dayStart, dayEnd)) {
      completedSurveyIds.add(response.getSurveyId());
    }

    List<String> completedWindows = new ArrayList<>();
    for (SurveyWindowDto window : windows) {
      if (window.getSurveyId() != null && completedSurveyIds.contains(window.getSurveyId())) {
        completedWindows.add(window.getName());
      }
    }

    Progress progress = progressRepository.findById(userId).orElse(null);
    Date weeklyNextAvailable = progress != null ? progress.getNextWeeklySurvey() : null;

    boolean studyConcluded = false;
    if (progress != null && progress.getStarted() != null) {
      LocalDate startDate = progress.getStarted().toInstant().atZone(clientZone).toLocalDate();
      long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(startDate, clientLocalDate);
      if (daysElapsed > 28) {
        studyConcluded = true;
        windows = new ArrayList<>();
      }
    }

    return new SurveyStatusResponse(windows, completedWindows, weeklyNextAvailable, studyConcluded);
  }

  /**
   * Gets the close time of the window currently open on the server.
   *
   * <p>This is the last piece of code that still asks what time it is where the server runs, and so
   * still assumes every participant is in the server's timezone. It survives only because {@link
   * edu.wpi.lemurs.api.endpoints.progress.ProgressService#recordDaily} uses it to set {@code
   * progress.next_daily_survey}, which is no longer what gates availability. It should be deleted
   * along with that column once submissions record their own local date and window.
   *
   * @deprecated Depends on the server's timezone. Do not use in new code.
   */
  @Deprecated
  public Date getEndOfCurrentAvailableSurvey(Date current) {
    LocalTime now = LocalTime.ofInstant(current.toInstant(), ZoneId.systemDefault());
    LocalDate today = LocalDate.ofInstant(current.toInstant(), ZoneId.systemDefault());

    LocalTime latest = now;

    for (SurveyAvailability availability : surveyAvailabilityRepository.findAll()) {
      if (!now.isAfter(availability.getOpenTime().toLocalTime())
          || !now.isBefore(availability.getCloseTime().toLocalTime())) {
        continue;
      }

      if (!latest.isAfter(availability.getCloseTime().toLocalTime())) {
        latest = availability.getCloseTime().toLocalTime();
      }
    }

    return Date.from(latest.atDate(today).atZone(ZoneId.systemDefault()).toInstant());
  }
}
