/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The {@link SurveyService} is a service that allows for {@link Survey} management */
@Service
@Transactional
public class SurveyService {
  private SurveyRepository surveyRepository;

  /** Autowires a {@link SurveyService} */
  public SurveyService(SurveyRepository surveyRepository) {
    this.surveyRepository = surveyRepository;
  }

  /**
   * gets the survey for a given id
   *
   * @param id the survey's id
   * @return the {@link Survey}
   * @throws EntityDoesNotExistException thrown if ther i sno survey with the given id
   */
  public Survey getSurvey(Integer id) throws EntityDoesNotExistException {
    Optional<Survey> survey = surveyRepository.findById(id);

    if (survey.isEmpty()) {
      throw new EntityDoesNotExistException();
    }
    return survey.get();
  }

  /**
   * Saves data to the database
   *
   * @param surveyDto The {@link surveyDto} representing the survey
   */
  public void saveSurvey(SurveyDto surveyDto) {
    Survey survey = new Survey(null, surveyDto.getName(), surveyDto.getTotal());
    surveyRepository.save(survey);
  }
}
