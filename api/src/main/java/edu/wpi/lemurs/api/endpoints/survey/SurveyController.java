/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import edu.wpi.lemurs.api.data.availability.SurveyAvailabilityService;
import edu.wpi.lemurs.api.endpoints.progress.AvailableResponse;
import edu.wpi.lemurs.api.endpoints.progress.ProgressService;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Creates endpoints for getting and answering surveys. */
@RestController
public class SurveyController {

  private SurveyService surveyService;
  private ProgressService progressService;
  private SurveyAvailabilityService surveyAvailabilityService;

  @Autowired
  public SurveyController(
      SurveyService surveyService,
      ProgressService progressService,
      SurveyAvailabilityService surveyAvailabilityService) {
    this.surveyService = surveyService;
    this.progressService = progressService;
    this.surveyAvailabilityService = surveyAvailabilityService;
  }

  /**
   * The <code>/survey/status</code> {@code GET} endpoint reports the survey windows and which of
   * them the participant has already completed on their own local date.
   *
   * @param localDate The participant's local calendar date, as {@code YYYY-MM-DD}.
   * @param tzId The participant's IANA timezone, e.g. {@code America/New_York}.
   */
  @GetMapping("/survey/status")
  public ResponseEntity<SurveyStatusResponse> getStatus(
      @RequestParam("localDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate,
      @RequestParam("tzId") String tzId) {
    try {
      ZoneId clientZone = ZoneId.of(tzId);

      return new ResponseEntity<>(
          surveyAvailabilityService.getStatus(localDate, clientZone), HttpStatus.OK);
    } catch (DateTimeException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping("/survey/daily")
  public ResponseEntity<List<SurveyApiResponse>> getDailySurveys() {
    try {
      List<SurveyApiResponse> surveys = surveyService.getDailySurveys();

      return new ResponseEntity<>(surveys, HttpStatus.OK);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping("/survey/weekly")
  public ResponseEntity<List<SurveyApiResponse>> getWeeklySurveys() {
    try {
      List<SurveyApiResponse> surveys = surveyService.getWeeklySurveys();

      return new ResponseEntity<>(surveys, HttpStatus.OK);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping("/survey/available")
  public ResponseEntity<List<AvailableResponse>> getAvailability() {
    try {

      List<AvailableResponse> availability = progressService.getSurveyAvailability();

      return new ResponseEntity<>(availability, HttpStatus.OK);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}
