/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link AnswerRepository}. */
public interface AnswerRepository extends CrudRepository<Answer, Integer> {}
