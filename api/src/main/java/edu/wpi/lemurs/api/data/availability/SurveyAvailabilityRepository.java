/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.data.availability;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link SurveyAvailabilityRepository}. */
public interface SurveyAvailabilityRepository extends CrudRepository<SurveyAvailability, String> {}
