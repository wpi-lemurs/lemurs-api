/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import edu.wpi.lemurs.api.endpoints.progress.AvailableResponse;
import edu.wpi.lemurs.api.endpoints.progress.ProgressService;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Creates endpoints for getting and answering surveys. */
@RestController
public class SurveyController {

  private SurveyService surveyService;
  private ProgressService progressService;

  @Autowired
  public SurveyController(SurveyService surveyService, ProgressService progressService) {
    this.surveyService = surveyService;
    this.progressService = progressService;
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
