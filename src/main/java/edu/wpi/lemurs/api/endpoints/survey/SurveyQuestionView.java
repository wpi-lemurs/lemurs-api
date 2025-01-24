/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link SurveyQuestionView} represents a question in a survey. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SurveyQuestionView {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false)
  private Integer surveyId;

  @Column(nullable = false)
  private Integer position;

  @Column(nullable = false)
  private String question;

  @Column(nullable = false)
  private String style;

  @Column(nullable = false)
  private List<String> options;

  @Column(nullable = false)
  private Integer parentQuestionId;

  @Column(nullable = false)
  private Integer prerequisiteQuestionId;

  @Column(nullable = false)
  private String prerequisiteAnswer;

  @Column(nullable = false)
  private List<String> requirements;
}
