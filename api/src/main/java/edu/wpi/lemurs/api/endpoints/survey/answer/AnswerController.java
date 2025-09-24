/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import edu.wpi.lemurs.api.endpoints.data.DataController;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting data. */
@RestController
public class AnswerController {

  private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);

  private AnswerService answerService;

  /** Autowires a {@link DataController} */
  @Autowired
  public AnswerController(AnswerService answerService) {
    this.answerService = answerService;}

  /** The <code>/data</code> {@code POST} endpoint saves the sent data. */
  @PostMapping("/survey/daily")
  public ResponseEntity<Void> recordAnswersDaily(
      @RequestBody CombinedSurveyResponseDto combinedSurveyResponseDto) {
    try {
      answerService.recordAnswersDaily(combinedSurveyResponseDto);

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * The <code>/survey/weekly</code> {@code POST} endpoint saves weekly survey data and returns the
   * survey response ID.
   */
  @PostMapping("/survey/weekly")
  public ResponseEntity<SurveySubmissionResponse> recordAnswersWeekly(
      @RequestBody CombinedSurveyResponseDto combinedSurveyResponseDto) {
    try {
      logger.info("Received weekly survey submission request");

      Integer surveyResponseId = answerService.recordAnswersWeekly(combinedSurveyResponseDto);

      SurveySubmissionResponse response = new SurveySubmissionResponse(surveyResponseId);

      logger.info(
          "Weekly survey submission completed successfully - survey response ID: {}",
          surveyResponseId);

      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      logger.warn("Unauthenticated weekly survey submission attempt");
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      logger.warn("Unauthorized weekly survey submission attempt");
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      logger.error("Error processing weekly survey submission", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
