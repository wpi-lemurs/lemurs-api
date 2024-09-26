/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting survey data */
@RestController
public class SurveyController {

  private SurveyService surveyService;

  /** Autowires a {@link SurveyController} */
  public SurveyController(SurveyService surveyService) {
    this.surveyService = surveyService;
  }

  /** {@code POST} endpoint saves the sent survey data */
  @PostMapping("/survey")
  public ResponseEntity<Void> saveSurvey(@RequestBody SurveyDto surveyDto) {
    try {
      surveyService.saveSurvey(surveyDto);

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
