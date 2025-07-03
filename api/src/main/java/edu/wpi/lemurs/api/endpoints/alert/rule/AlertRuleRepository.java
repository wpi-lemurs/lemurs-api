/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.rule;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link AlertRule}. */
public interface AlertRuleRepository extends CrudRepository<AlertRule, Integer> {
  /** Finds all alert rules for a given question ID. */
  List<AlertRule> findByQuestionId(Integer questionId);
}