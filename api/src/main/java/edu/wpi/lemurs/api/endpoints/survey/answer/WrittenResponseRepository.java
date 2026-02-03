/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing WritingResponse entities. */
@Repository
public interface WrittenResponseRepository extends CrudRepository<WrittenResponse, Integer> {
  /** Finds all written responses for a specific survey response. */
  List<WrittenResponse> findBySurvey_response_id(Integer surveyResponseId);

  /** Checks if any written responses exist for a survey response. */
  boolean existsBySurvey_response_id(Integer surveyResponseId);
}
