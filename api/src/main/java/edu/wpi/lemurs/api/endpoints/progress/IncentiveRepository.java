/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link IncentiveRepository}. */
public interface IncentiveRepository extends CrudRepository<Incentive, Integer> {}
