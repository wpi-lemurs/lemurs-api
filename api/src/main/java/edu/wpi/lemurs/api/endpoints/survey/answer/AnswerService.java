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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AnswerService {

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

  public void recordAnswersWeekly(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.USER);

    // TODO: Check that the survey are all weekly surveys.

    recordAnswers(combinedSurveyResponseDto);

    progressService.recordWeekly(combinedSurveyResponseDto.getTimestamp());
  }

  private void recordAnswers(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {
    Integer userId = securityService.getUser().getId();

    for (SurveyResponseDto surveyResponseDto : combinedSurveyResponseDto.getSurveys()) {
      SurveyResponse survey =
          new SurveyResponse(
              null,
              userId,
              surveyResponseDto.getId(),
              combinedSurveyResponseDto.getTimestamp(),
              combinedSurveyResponseDto.getNotificationStart());
      survey = surveyResponseRepository.save(survey);

      List<Answer> answers = new ArrayList<>();
      for (AnswerDto answerDto : surveyResponseDto.getAnswers()) {
        answers.add(
            new Answer(null, survey.getId(), answerDto.getId(), answerDto.getAnswer()));
      }
      answerRepository.saveAll(answers);

      // Check for danger alerts in this survey's answers
      dangerAlertTriggerService.checkAnswersForDangerAlerts(userId, surveyResponseDto.getAnswers());
    }
  }
}
