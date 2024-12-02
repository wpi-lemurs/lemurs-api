/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link SurveyRepository}. */
public interface SurveyRepository extends CrudRepository<Survey, Integer> {
  Iterable<Survey> findByIsDailyTrue();

  Iterable<Survey> findByIsWeeklyTrue();
}
