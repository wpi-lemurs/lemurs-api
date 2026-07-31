/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link SurveyResponse} represents a response to a survey. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SurveyResponse {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false, name = "app_user_id")
  private Integer userID;

  @Column(nullable = false)
  private Integer surveyId;

  @Column(nullable = false)
  private Date timestamp;

  @Column(nullable = false)
  private Date notificationStart;

  /**
   * Identifies one submission attempt, so a retry of that attempt is not stored twice.
   *
   * <p>Nullable: rows written before submissions had an identity do not have one, and a client that
   * omits it still submits successfully, just without the duplicate protection.
   */
  @Column(name = "client_submission_id", length = 64)
  private String clientSubmissionId;
}
