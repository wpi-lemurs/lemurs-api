/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import edu.wpi.lemurs.api.endpoints.alert.danger.DangerAlertEmailService;
import edu.wpi.lemurs.api.endpoints.alert.rule.AlertRule;
import edu.wpi.lemurs.api.endpoints.alert.rule.AlertRuleRepository;
import edu.wpi.lemurs.api.endpoints.progress.ProgressService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AnswerService {
  private static final Logger logger = LogManager.getLogger(AnswerService.class);

  private SecurityService securityService;
  private AnswerRepository answerRepository;
  private SurveyResponseRepository surveyResponseRepository;
  private ProgressService progressService;
  private DangerAlertEmailService dangerAlertEmailService;
  private AlertRuleRepository alertRuleRepository;

  @Autowired
  public AnswerService(
      SecurityService securityService,
      AnswerRepository answerRepository,
      SurveyResponseRepository surveyResponseRepository,
      ProgressService progressService,
      DangerAlertEmailService dangerAlertEmailService,
      AlertRuleRepository alertRuleRepository) {
    this.securityService = securityService;
    this.answerRepository = answerRepository;
    this.surveyResponseRepository = surveyResponseRepository;
    this.progressService = progressService;
    this.dangerAlertEmailService = dangerAlertEmailService;
    this.alertRuleRepository = alertRuleRepository;
  }

  public boolean recordAnswersDaily(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.USER);

    // TODO: Check that the survey are all daily surveys.

    boolean alertTriggered = recordAnswers(combinedSurveyResponseDto);

    progressService.recordDaily(combinedSurveyResponseDto.getTimestamp());
    return alertTriggered;
  }

  public boolean recordAnswersWeekly(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.USER);

    // TODO: Check that the survey are all weekly surveys.

    boolean alertTriggered = recordAnswers(combinedSurveyResponseDto);

    progressService.recordWeekly(combinedSurveyResponseDto.getTimestamp());
    return alertTriggered;
  }

  private boolean recordAnswers(CombinedSurveyResponseDto combinedSurveyResponseDto)
      throws UnauthenticatedException, UnauthorizedException {

    boolean alertTriggered = false;
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
        answers.add(new Answer(null, survey.getId(), answerDto.getId(), answerDto.getAnswer()));

        // --- START OF DYNAMIC ALERT LOGIC ---
        if (checkAndTriggerAlerts(userId, answerDto)) {
          alertTriggered = true;
        }
        // --- END OF DYNAMIC ALERT LOGIC ---
      }
      answerRepository.saveAll(answers);
    }
    return alertTriggered;
  }

  /**
   * Checks an answer against configured alert rules and triggers an alert if a condition is met.
   *
   * @param userId The ID of the user who provided the answer.
   * @param answerDto The answer to check.
   * @return {@code true} if an alert was triggered, {@code false} otherwise.
   */
  private boolean checkAndTriggerAlerts(Integer userId, AnswerDto answerDto) {
    List<AlertRule> rules = alertRuleRepository.findByQuestionId(answerDto.getId());
    if (rules.isEmpty()) {
      return false;
    }

    for (AlertRule rule : rules) {
      try {
        // This implementation assumes numeric comparisons. It could be expanded to support other
        // types.
        int answerValue = Integer.parseInt(answerDto.getAnswer());
        int thresholdValue = Integer.parseInt(rule.getThreshold());

        if (rule.getOperator().evaluate(answerValue, thresholdValue)) {
          String reason =
              rule.getReasonTemplate()
                  .replace("{answer}", String.valueOf(answerValue))
                  .replace("{questionId}", rule.getQuestionId().toString())
                  .replace("{operator}", rule.getOperator().getSymbol())
                  .replace("{threshold}", rule.getThreshold());

          dangerAlertEmailService.sendAlertWithoutAuthCheck(
              userId, Collections.singletonList(reason));

          // An alert was triggered for this answer, so we can stop checking other rules.
          return true;
        }
      } catch (NumberFormatException e) {
        logger.warn(
            "Could not evaluate alert rule ID {} for question ID {}. Answer or threshold was not an integer.",
            rule.getId(),
            answerDto.getId());
      } catch (MailException
          | MessagingException
          | UnsupportedEncodingException
          | EntityDoesNotExistException e) {
        logger.error("Failed to send danger alert email for user ID {}", userId, e);
      }
    }
    return false;
  }
}
