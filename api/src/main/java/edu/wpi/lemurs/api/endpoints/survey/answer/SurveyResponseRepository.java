/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/** A {@link CrudRepository} for a {@link SurveyResponseRepository}. */
public interface SurveyResponseRepository extends CrudRepository<SurveyResponse, Integer> {

  /**
   * Finds the surveys a user submitted between two instants.
   *
   * <p>Callers pass the exact instants that bound the participant's local day, so this stays
   * correct no matter which timezone that participant is in.
   *
   * @param userId The user to look up.
   * @param from Inclusive lower bound.
   * @param to Exclusive upper bound.
   */
  @Query(
      "SELECT r FROM SurveyResponse r "
          + "WHERE r.userID = :userId AND r.timestamp >= :from AND r.timestamp < :to")
  List<SurveyResponse> findByUserBetween(
      @Param("userId") Integer userId, @Param("from") Date from, @Param("to") Date to);

  /**
   * Finds the responses already stored for one submission attempt.
   *
   * <p>Scoped to the user, matching the unique index: two participants generating the same id must
   * not block one another.
   */
  @Query(
      "SELECT r FROM SurveyResponse r "
          + "WHERE r.userID = :userId AND r.clientSubmissionId = :clientSubmissionId")
  List<SurveyResponse> findByUserAndClientSubmissionId(
      @Param("userId") Integer userId, @Param("clientSubmissionId") String clientSubmissionId);
}
