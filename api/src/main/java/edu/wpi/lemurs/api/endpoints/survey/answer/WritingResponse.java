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
public class WritingResponse {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false, name = "survey_response_id")
  private Integer survey_response_id;

  @Column(nullable = false, name = "written_question_id")
  private Integer written_question_id;

  @Column(nullable = false)
  private String data;

  @Column(nullable = false)
  private Date timestamp;

  @Column(nullable = false)
  private Date notificationStart;
}
