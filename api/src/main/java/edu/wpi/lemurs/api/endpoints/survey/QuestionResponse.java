/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionResponse {
  private Integer id;
  private String question;
  private String style;
  private List<String> options;
  private Integer parentQuestionId;
  private Integer prerequisiteQuestionId;
  private String prerequisiteAnswer;
  private Boolean isTriggerQuestion;
  private Integer triggerThreshold;
}
