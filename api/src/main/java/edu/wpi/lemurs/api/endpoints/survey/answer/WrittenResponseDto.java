/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A DTO for transferring writing response data between API and clients. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WrittenResponseDto {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("survey_response_id")
  @JsonAlias({"surveyResponseId"})
  private Integer survey_response_id;

  @JsonProperty("written_question_id")
  @JsonAlias({"writtenQuestionId"})
  private Integer written_question_id;

  @JsonProperty("written_data")
  private String written_data;

  @JsonProperty("timestamp")
  private Date timestamp;
}
