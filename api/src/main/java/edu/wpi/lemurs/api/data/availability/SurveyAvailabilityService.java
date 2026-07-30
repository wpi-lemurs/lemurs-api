/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.data.availability;

import edu.wpi.lemurs.api.endpoints.survey.SurveyStatusResponse;
import edu.wpi.lemurs.api.endpoints.survey.SurveyWindowDto;
import edu.wpi.lemurs.api.endpoints.survey.answer.SurveyResponse;
import edu.wpi.lemurs.api.endpoints.survey.answer.SurveyResponseRepository;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private SurveyWindowProperties surveyWindowProperties;

  /** Autowires a {@link SurveyAvailabilityService}. */
  @Autowired
  public SurveyAvailabilityService(
      SecurityService securityService,
      SurveyAvailabilityRepository surveyAvailabilityRepository,
      SurveyResponseRepository surveyResponseRepository,
      SurveyWindowProperties surveyWindowProperties) {
    this.securityService = securityService;
    this.surveyAvailabilityRepository = surveyAvailabilityRepository;
    this.surveyResponseRepository = surveyResponseRepository;
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

    return new SurveyStatusResponse(windows, completedWindows);
  }

  /**
   * Gets all of the available survey group names for the user.
   *
   * @return A list of survey group names like "morning" or "afternoon".
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public List<String> getAvailableSurveyGroups()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.USER);

    ArrayList<String> surveyGroups = new ArrayList<>();

    LocalTime now = LocalTime.now();

    for (SurveyAvailability availability : surveyAvailabilityRepository.findAll()) {
      if (!now.isAfter(availability.getOpenTime().toLocalTime())
          || !now.isBefore(availability.getCloseTime().toLocalTime())) {
        continue;
      }
      surveyGroups.add(availability.getName());
    }

    return surveyGroups;
  }

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

  /**
   * Gets the next available survey time.
   *
   * @return The next available survey time. If there is a survey available right now, returns that
   *     surveys open time.
   */
  public Date getNextAvailableSurveyOpen(Date next) {
    Date current = new Date();

    LocalTime now =
        LocalTime.ofInstant(
            current.after(next) ? current.toInstant() : next.toInstant(), ZoneId.systemDefault());
    LocalDate today =
        LocalDate.ofInstant(
            current.after(next) ? current.toInstant() : next.toInstant(), ZoneId.systemDefault());

    LocalDateTime earliest = null;
    Duration minDiff = Duration.ofDays(99);

    for (SurveyAvailability availability : surveyAvailabilityRepository.findAll()) {
      if (!now.isAfter(availability.getOpenTime().toLocalTime())
          || !now.isBefore(availability.getCloseTime().toLocalTime())) {
        Duration diff = Duration.between(now, availability.getOpenTime().toLocalTime());
        if (diff.isNegative()) {
          diff = diff.plusDays(1);
        }

        if (diff.toSeconds() < minDiff.toSeconds()) {
          minDiff = diff;
          if (!now.isAfter(availability.getOpenTime().toLocalTime())) {
            earliest = availability.getOpenTime().toLocalTime().atDate(today);
          } else {
            earliest = availability.getOpenTime().toLocalTime().atDate(today.plusDays(1));
          }
        }
        continue;
      }
      return Date.from(
          availability
              .getOpenTime()
              .toLocalTime()
              .atDate(today)
              .atZone(ZoneId.systemDefault())
              .toInstant());
    }

    return Date.from(earliest.atZone(ZoneId.systemDefault()).toInstant());
  }
}
