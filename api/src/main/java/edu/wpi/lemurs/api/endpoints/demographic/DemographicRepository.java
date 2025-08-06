/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.demographic;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link DemographicRepository}. */
public interface DemographicRepository extends CrudRepository<Demographic, Integer> {
  Iterable<Demographic> findByUserID(Integer userID);
}
