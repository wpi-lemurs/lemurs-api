/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.util.ArrayList;
import java.util.List;
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

  /** Autowires a {@link SurveyService}. */
  @Autowired
  public SurveyService(
      SecurityService securityService,
      SurveyRepository surveyRepository,
      SurveyQuestionViewRepository surveyQuestionViewRepository) {
    this.securityService = securityService;
    this.surveyRepository = surveyRepository;
    this.surveyQuestionViewRepository = surveyQuestionViewRepository;
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

    Survey survey = surveyRepository.findById(0).get();

    List<SurveyApiResponse> surveys = new ArrayList<>();
    List<QuestionResponse> questions = new ArrayList<>();
    SurveyApiResponse surveyResponse =
        new SurveyApiResponse(survey.getId(), survey.getName(), questions);
    for (SurveyQuestionView question :
        surveyQuestionViewRepository.findBySurveyIdOrderByPosition(survey.getId())) {
      questions.add(
          new QuestionResponse(
              question.getId(),
              question.getQuestion(),
              question.getStyle(),
              question.getOptions(),
              question.getParentQuestionId(),
              question.getPrerequisiteQuestionId(),
              question.getPrerequisiteAnswer()));
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

    List<SurveyApiResponse> surveys = new ArrayList<>();
    Survey survey = surveyRepository.findById(2).get();
    List<QuestionResponse> questions = new ArrayList<>();
    SurveyApiResponse surveyResponse =
        new SurveyApiResponse(survey.getId(), survey.getName(), questions);
    for (SurveyQuestionView question :
        surveyQuestionViewRepository.findBySurveyIdOrderByPosition(survey.getId())) {
      questions.add(
          new QuestionResponse(
              question.getId(),
              question.getQuestion(),
              question.getStyle(),
              question.getOptions(),
              question.getParentQuestionId(),
              question.getPrerequisiteQuestionId(),
              question.getPrerequisiteAnswer()));
    }
    surveys.add(surveyResponse);

    return surveys;
  }
}
