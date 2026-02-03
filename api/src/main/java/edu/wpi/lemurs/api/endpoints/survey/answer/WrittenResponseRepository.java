/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repository for managing WritingResponse entities. */
@Repository
public interface WrittenResponseRepository extends CrudRepository<WrittenResponse, Integer> {
  /** Finds all written responses for a specific survey response. */
  @Query("SELECT w FROM WrittenResponse w WHERE w.survey_response_id = :surveyResponseId")
  List<WrittenResponse> findBySurveyResponseId(@Param("surveyResponseId") Integer surveyResponseId);

  /** Checks if any written responses exist for a survey response. */
  @Query(
      "SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM WrittenResponse w WHERE w.survey_response_id = :surveyResponseId")
  boolean existsBySurveyResponseId(@Param("surveyResponseId") Integer surveyResponseId);
}
