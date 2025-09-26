/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repository interface for AudioResponse entity operations. */
@Repository
public interface AudioResponseRepository extends JpaRepository<AudioResponse, Integer> {
  /** Finds all audio responses for a specific survey response. */
  List<AudioResponse> findBySurveyResponseIdOrderByTimestampDesc(Integer surveyResponseId);

  /** Finds audio responses by question ID. */
  List<AudioResponse> findByAudioQuestionIdOrderByTimestampDesc(Integer audioQuestionId);

  /** Finds audio responses within a time range. */
  List<AudioResponse> findByTimestampBetweenOrderByTimestampDesc(
      LocalDateTime startTime, LocalDateTime endTime);

  /** Finds the most recent audio response for a survey response. */
  Optional<AudioResponse> findTopBySurveyResponseIdOrderByTimestampDesc(Integer surveyResponseId);

  /** Finds audio responses created within a specific time window. */
  List<AudioResponse> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

  /** Counts audio responses for a survey response. */
  Long countBySurveyResponseId(Integer surveyResponseId);

  /** Gets total audio data size for a survey response using native SQL. */
  @Query(
      value =
          "SELECT SUM(octet_length(audio_data)) FROM audio_response WHERE survey_response_id = :surveyResponseId",
      nativeQuery = true)
  Long getTotalAudioDataSizeBySurveyResponseId(@Param("surveyResponseId") Integer surveyResponseId);

  /** Gets average audio data size for a specific question using native SQL. */
  @Query(
      value =
          "SELECT AVG(octet_length(audio_data)) FROM audio_response WHERE audio_question_id = :questionId",
      nativeQuery = true)
  Double getAverageAudioDataSizeByQuestionId(@Param("questionId") Integer questionId);

  /** Finds audio responses with data size larger than specified threshold using native SQL. */
  @Query(
      value =
          "SELECT * FROM audio_response WHERE octet_length(audio_data) >= :minSize ORDER BY timestamp DESC",
      nativeQuery = true)
  List<AudioResponse> findWithMinimumAudioDataSize(@Param("minSize") Integer minSize);

  /** Gets audio responses created in the last N hours. */
  @Query("SELECT ar FROM AudioResponse ar WHERE ar.createdAt >= :sinceTime")
  List<AudioResponse> findRecentAudioResponses(@Param("sinceTime") LocalDateTime sinceTime);

  /** Finds audio responses by survey response and timestamp range. */
  List<AudioResponse> findBySurveyResponseIdAndTimestampBetweenOrderByTimestampDesc(
      Integer surveyResponseId, LocalDateTime startTime, LocalDateTime endTime);

  /** Gets all audio responses for multiple survey responses. */
  @Query(
      "SELECT ar FROM AudioResponse ar WHERE ar.surveyResponseId IN :surveyResponseIds ORDER BY ar.timestamp DESC")
  List<AudioResponse> findBySurveyResponseIdIn(
      @Param("surveyResponseIds") List<Integer> surveyResponseIds);
}
