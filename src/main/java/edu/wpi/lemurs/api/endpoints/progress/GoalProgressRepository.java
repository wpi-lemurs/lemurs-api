/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link GoalProgress}. */
public interface GoalProgressRepository extends CrudRepository<GoalProgress, UserGoal> {
  Iterable<GoalProgress> findByUserID(Integer userID);
}
