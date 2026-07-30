/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A survey window expressed as plain wall-clock times.
 *
 * <p>These are deliberately not instants. "The morning survey opens at 08:00" means 08:00 in the
 * participant's own timezone, so the client is responsible for deciding whether a window is
 * currently open. The API only states what the windows are.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SurveyWindowDto {
  private String name;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  private LocalTime openTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
  private LocalTime closeTime;

  private Integer surveyId;
}
