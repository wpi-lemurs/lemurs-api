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
public class WritingResponseDto {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("survey_response_id")
  @JsonAlias({"surveyResponseId"})
  private Integer surveyResponseId;

  @JsonProperty("written_question_id")
  @JsonAlias({"writtenQuestionId"})
  private Integer writtenQuestionId;

  @JsonProperty("data")
  private String data;

  @JsonProperty("timestamp")
  private Date timestamp;

  @JsonProperty("notification_start")
  @JsonAlias({"notificationStart"})
  private Date notificationStart;
}
