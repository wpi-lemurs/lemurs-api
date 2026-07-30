/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Everything the client needs to decide whether a daily survey is available.
 *
 * <p>Note that this response contains no absolute timestamps and nothing that depends on where the
 * server is running. It reports what the windows are and what the participant has already submitted
 * on the local date they asked about; the client combines the two against its own clock.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SurveyStatusResponse {
  private List<SurveyWindowDto> windows;

  /** Names of the windows the participant has already completed on the requested local date. */
  private List<String> completedWindows;
}
