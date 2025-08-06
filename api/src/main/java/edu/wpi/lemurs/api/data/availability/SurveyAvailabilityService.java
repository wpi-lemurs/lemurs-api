/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.data.availability;

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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The {@link SurveyAvailabilityService} is a service that allows for {@link Data} management. */
@Service
@Transactional
public class SurveyAvailabilityService {

  private SecurityService securityService;
  private SurveyAvailabilityRepository surveyAvailabilityRepository;

  /** Autowires a {@link SurveyAvailabilityService}. */
  @Autowired
  public SurveyAvailabilityService(
      SecurityService securityService, SurveyAvailabilityRepository surveyAvailabilityRepository) {
    this.securityService = securityService;
    this.surveyAvailabilityRepository = surveyAvailabilityRepository;
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
