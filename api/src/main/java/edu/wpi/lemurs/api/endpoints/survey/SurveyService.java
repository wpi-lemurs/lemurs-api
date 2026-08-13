/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import edu.wpi.lemurs.api.data.availability.SurveyWindowProperties;
import edu.wpi.lemurs.api.endpoints.alert.trigger.DangerAlertTrigger;
import edu.wpi.lemurs.api.endpoints.alert.trigger.DangerAlertTriggerService;
import edu.wpi.lemurs.api.endpoints.demographic.DemographicService;
import edu.wpi.lemurs.api.exceptions.BadRequestException;
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
  private SurveyWindowProperties surveyWindowProperties;
  private DemographicService demographicService;
  private DangerAlertTriggerService dangerAlertTriggerService;

  public static final Integer MORNING_SURVEY_ID = 0;
  public static final Integer AFTERNOON_SURVEY_ID = 1;
  public static final Integer WEEKLY_SURVEY_ID = 2;

  /** Autowires a {@link SurveyService}. */
  @Autowired
  public SurveyService(
      SecurityService securityService,
      SurveyRepository surveyRepository,
      SurveyQuestionViewRepository surveyQuestionViewRepository,
      SurveyWindowProperties surveyWindowProperties,
      DemographicService demographicService,
      DangerAlertTriggerService dangerAlertTriggerService) {
    this.securityService = securityService;
    this.surveyRepository = surveyRepository;
    this.surveyQuestionViewRepository = surveyQuestionViewRepository;
    this.surveyWindowProperties = surveyWindowProperties;
    this.demographicService = demographicService;
    this.dangerAlertTriggerService = dangerAlertTriggerService;
  }

  /**
   * Gets the questions for the daily survey belonging to the given window.
   *
   * <p>The client says which window it is in, because only the client knows the participant's
   * timezone. The server previously decided this from its own clock, which meant a participant
   * outside Eastern time was served an empty list during their own morning.
   *
   * @param windowName The window the participant is currently in, e.g. {@code morning}.
   * @throws BadRequestException Thrown if the window is not one the system knows about.
   */
  public List<SurveyApiResponse> getDailySurveys(String windowName)
      throws UnauthenticatedException, UnauthorizedException, BadRequestException {
    securityService.assertHasRole(LemursRole.USER);

    Integer surveyId = surveyWindowProperties.getWindowSurveyIds().get(windowName);
    if (surveyId == null) {
      throw new BadRequestException("Unknown survey window: " + windowName);
    }

    return List.of(buildSurveyResponse(surveyId));
  }

  /** Assembles one survey's questions, filtered by demographics and annotated with triggers. */
  private SurveyApiResponse buildSurveyResponse(Integer surveyId)
      throws UnauthenticatedException, UnauthorizedException {
    Map<String, String> demographics = demographicService.getDemographicMap();
    Map<Integer, DangerAlertTrigger> activeTriggers =
        dangerAlertTriggerService.getActiveTriggersMap();

    Survey survey = surveyRepository.findById(surveyId).get();
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

      DangerAlertTrigger trigger = activeTriggers.get(question.getId());
      boolean isTriggerQuestion = trigger != null;
      Integer triggerThreshold = isTriggerQuestion ? trigger.getThreshold() : null;

      questions.add(
          new QuestionResponse(
              question.getId(),
              question.getQuestion(),
              question.getStyle(),
              question.getOptions(),
              question.getParentQuestionId(),
              question.getPrerequisiteQuestionId(),
              question.getPrerequisiteAnswer(),
              isTriggerQuestion,
              triggerThreshold));
    }

    return surveyResponse;
  }

  public List<SurveyApiResponse> getWeeklySurveys()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    return List.of(buildSurveyResponse(WEEKLY_SURVEY_ID));
  }
}
