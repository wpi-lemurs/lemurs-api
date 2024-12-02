/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

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

  @Autowired
  public AnswerService(
      SecurityService securityService,
      AnswerRepository answerRepository,
      SurveyResponseRepository surveyResponseRepository,
      ProgressService progressService) {
    this.securityService = securityService;
    this.answerRepository = answerRepository;
    this.surveyResponseRepository = surveyResponseRepository;
    this.progressService = progressService;
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

    for (SurveyResponseDto surveyResponseDto : combinedSurveyResponseDto.getSurveys()) {
      SurveyResponse survey =
          new SurveyResponse(
              null,
              securityService.getUser().getId(),
              surveyResponseDto.getId(),
              combinedSurveyResponseDto.getTimestamp());
      survey = surveyResponseRepository.save(survey);

      List<Answer> answers = new ArrayList<>();
      for (AnswerDto answerDto : surveyResponseDto.getAnswers()) {
        answers.add(new Answer(null, survey.getId(), answerDto.getId(), answerDto.getAnswer()));
      }
      answerRepository.saveAll(answers);
    }
  }
}
