/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import edu.wpi.lemurs.api.endpoints.alert.trigger.DangerAlertTriggerService;
import edu.wpi.lemurs.api.endpoints.progress.ProgressService;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AnswerService {

  private static final Logger logger = LoggerFactory.getLogger(AnswerService.class);

  private SecurityService securityService;
  private AnswerRepository answerRepository;
  private SurveyResponseRepository surveyResponseRepository;
  private ProgressService progressService;
  private DangerAlertTriggerService dangerAlertTriggerService;

  @Autowired
  public AnswerService(
      SecurityService securityService,
      AnswerRepository answerRepository,
      SurveyResponseRepository surveyResponseRepository,
      ProgressService progressService,
      DangerAlertTriggerService dangerAlertTriggerService) {
    this.securityService = securityService;
    this.answerRepository = answerRepository;
    this.surveyResponseRepository = surveyResponseRepository;
    this.progressService = progressService;
    this.dangerAlertTriggerService = dangerAlertTriggerService;
  }

  public void recordAnswersDaily(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.USER);

    // TODO: Check that the survey are all daily surveys.

    recordAnswers(combinedSurveyResponseDto);

    progressService.recordDaily(combinedSurveyResponseDto.getTimestamp());
  }

  public Integer recordAnswersWeekly(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.USER);

    // TODO: Check that the survey are all weekly surveys.

    Integer userId = securityService.getUser().getId();
    logger.info(
        "Recording weekly survey answers for user {} at {}",
        userId,
        combinedSurveyResponseDto.getTimestamp());

    List<Integer> surveyResponseIds = recordAnswers(combinedSurveyResponseDto);

    // Return the first survey response ID (primary survey for linking)
    if (!surveyResponseIds.isEmpty()) {
      Integer primarySurveyResponseId = surveyResponseIds.get(0);

      // Record weekly progress with survey response ID to check for audio/written bonuses
      progressService.recordWeekly(
          combinedSurveyResponseDto.getTimestamp(), primarySurveyResponseId);

      logger.info(
          "Weekly survey submission completed for user {} - primary survey response ID: {}",
          userId,
          primarySurveyResponseId);
      return primarySurveyResponseId;
    } else {
      logger.warn("No survey responses created for user {}", userId);
      throw new RuntimeException("No survey responses were created");
    }
  }

  private List<Integer> recordAnswers(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {
    Integer userId = securityService.getUser().getId();
    List<Integer> surveyResponseIds = new ArrayList<>();

    for (SurveyResponseDto surveyResponseDto : combinedSurveyResponseDto.getSurveys()) {
      SurveyResponse survey =
          new SurveyResponse(
              null,
              userId,
              surveyResponseDto.getId(),
              combinedSurveyResponseDto.getTimestamp(),
              combinedSurveyResponseDto.getNotificationStart());
      survey = surveyResponseRepository.save(survey);
      surveyResponseIds.add(survey.getId());

      List<Answer> answers = new ArrayList<>();
      for (AnswerDto answerDto : surveyResponseDto.getAnswers()) {
        answers.add(
            new Answer(null, survey.getId(), answerDto.getQuestionId(), answerDto.getAnswer()));
      }
      answerRepository.saveAll(answers);

      // Check for danger alerts in this survey's answers
      dangerAlertTriggerService.checkAnswersForDangerAlerts(userId, surveyResponseDto.getAnswers());
    }

    return surveyResponseIds;
  }
}
