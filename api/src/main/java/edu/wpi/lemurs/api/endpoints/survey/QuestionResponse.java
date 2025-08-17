/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  // Use @JsonProperty to ensure the JSON key is exactly "isTriggerQuestion"
  @JsonProperty("isTriggerQuestion")
  private Boolean isTriggerQuestion;

  private Integer triggerThreshold;
}
