/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CombinedSurveyResponseDto {
  private Date timestamp;
  private List<SurveyResponseDto> surveys;
  private Date notificationStart;

  /**
   * Identifies this submission attempt so retrying it does not store it twice.
   *
   * <p>Generated once by the client when the participant submits and reused for every retry of that
   * attempt. Optional: a client that omits it still submits, but without duplicate protection.
   */
  private String clientSubmissionId;
}
