/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link SurveyQuestionView}. */
public interface SurveyQuestionViewRepository extends CrudRepository<SurveyQuestionView, Integer> {

  Iterable<SurveyQuestionView> findBySurveyIdOrderByPosition(Integer surveyId);
}
