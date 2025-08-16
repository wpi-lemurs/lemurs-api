/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import edu.wpi.lemurs.api.data.availability.SurveyAvailabilityService;
import edu.wpi.lemurs.api.endpoints.demographic.DemographicService;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The {@link SurveyService} is a service that allows for {@link Data} management. */
@Service
@Transactional
public class SurveyService {

  private SecurityService securityService;
  private SurveyRepository surveyRepository;
  private SurveyQuestionViewRepository surveyQuestionViewRepository;
  private SurveyAvailabilityService surveyAvailabilityService;
  private DemographicService demographicService;
  // private DangerAlertTriggerService dangerAlertTriggerService;

  public static final Integer MORNING_SURVEY_ID = 0;
  public static final Integer AFTERNOON_SURVEY_ID = 1;
  public static final Integer WEEKLY_SURVEY_ID = 2;

  /** Autowires a {@link SurveyService}. */
  @Autowired
  public SurveyService(
      SecurityService securityService,
      SurveyRepository surveyRepository,
      SurveyQuestionViewRepository surveyQuestionViewRepository,
      SurveyAvailabilityService surveyAvailabilityService,
      DemographicService demographicService) {
    // DangerAlertTriggerService dangerAlertTriggerService) {
    this.securityService = securityService;
    this.surveyRepository = surveyRepository;
    this.surveyQuestionViewRepository = surveyQuestionViewRepository;
    this.surveyAvailabilityService = surveyAvailabilityService;
    this.demographicService = demographicService;
    // this.dangerAlertTriggerService = dangerAlertTriggerService;
  }

  /**
   * Gets all of the daily surveys for the user.
   *
   * @return A list of {@link SurveyApiResponse}s with each daily survey.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public List<SurveyApiResponse> getDailySurveys()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    List<SurveyApiResponse> surveys = new ArrayList<>();
    Map<String, String> demographics = demographicService.getDemographicMap();
    // Map<Integer, DangerAlertTrigger> activeTriggers =
    //     dangerAlertTriggerService.getActiveTriggersMap();

    for (String surveyGroup : surveyAvailabilityService.getAvailableSurveyGroups()) {
      // TODO: Create a table matchin the group names to surveys, and fetching them appropriately.
      Survey survey = null;
      if (surveyGroup.equals("morning")) {
        survey = surveyRepository.findById(MORNING_SURVEY_ID).get();
      } else {
        survey = surveyRepository.findById(AFTERNOON_SURVEY_ID).get();
      }
      List<QuestionResponse> questions = new ArrayList<>();
      SurveyApiResponse surveyResponse =
          new SurveyApiResponse(survey.getId(), survey.getName(), questions);
      for (SurveyQuestionView question :
          surveyQuestionViewRepository.findBySurveyIdOrderByPosition(survey.getId())) {

        if (question.getRequirements() != null) {
          boolean meetsRequirements = true;
          for (String requirement : question.getRequirements()) {
            String r = requirement.toLowerCase();
            if (!demographics.containsKey(r) || !demographics.get(r).equalsIgnoreCase("true")) {
              meetsRequirements = false;
              break;
            }
          }

          if (!meetsRequirements) {
            continue;
          }
        }

        // DangerAlertTrigger trigger = activeTriggers.get(question.getId());
        // boolean isTriggerQuestion = trigger != null;
        // Integer triggerThreshold = isTriggerQuestion ? trigger.getThreshold() : null;
        questions.add(
            new QuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getStyle(),
                question.getOptions(),
                question.getParentQuestionId(),
                question.getPrerequisiteQuestionId(),
                question.getPrerequisiteAnswer()));
        // isTriggerQuestion,
        // triggerThreshold));
      }
      surveys.add(surveyResponse);
    }

    return surveys;
  }

  /**
   * Gets all of the daily surveys for the user.
   *
   * @return A list of {@link SurveyApiResponse}s with each daily survey.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public List<SurveyApiResponse> getWeeklySurveys()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    // TOOD: Find a better way to find the weekly survey.
    List<SurveyApiResponse> surveys = new ArrayList<>();
    Map<String, String> demographics = demographicService.getDemographicMap();
    // Map<Integer, DangerAlertTrigger> activeTriggers =
    // //     dangerAlertTriggerService.getActiveTriggersMap();
    Survey survey = surveyRepository.findById(WEEKLY_SURVEY_ID).get();
    List<QuestionResponse> questions = new ArrayList<>();
    SurveyApiResponse surveyResponse =
        new SurveyApiResponse(survey.getId(), survey.getName(), questions);
    for (SurveyQuestionView question :
        surveyQuestionViewRepository.findBySurveyIdOrderByPosition(survey.getId())) {

      if (question.getRequirements() != null) {
        boolean meetsRequirements = true;
        for (String requirement : question.getRequirements()) {
          String r = requirement.toLowerCase();
          if (!demographics.containsKey(r) || !demographics.get(r).equalsIgnoreCase("true")) {
            meetsRequirements = false;
            break;
          }
        }

        if (!meetsRequirements) {
          continue;
        }
      }

      // DangerAlertTrigger trigger = activeTriggers.get(question.getId());
      // boolean isTriggerQuestion = trigger != null;
      // Integer triggerThreshold = isTriggerQuestion ? trigger.getThreshold() : null;
      questions.add(
          new QuestionResponse(
              question.getId(),
              question.getQuestion(),
              question.getStyle(),
              question.getOptions(),
              question.getParentQuestionId(),
              question.getPrerequisiteQuestionId(),
              question.getPrerequisiteAnswer()));
      // isTriggerQuestion,
      // triggerThreshold));
    }
    surveys.add(surveyResponse);

    return surveys;
  }
}
