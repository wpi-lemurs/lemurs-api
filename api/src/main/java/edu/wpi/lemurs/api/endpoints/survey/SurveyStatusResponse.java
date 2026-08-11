/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
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

  /**
   * When the weekly survey next becomes available, or null if it is available now.
   *
   * <p>Unlike the daily windows this genuinely is an absolute instant: the weekly survey is gated
   * on "seven days since the last one" rather than on a time of day, so it needs no local
   * interpretation.
   */
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
      timezone = "UTC")
  private Date weeklyNextAvailable;

  private Boolean studyConcluded = false;

  public SurveyStatusResponse(
      List<SurveyWindowDto> windows,
      List<String> completedWindows,
      Date weeklyNextAvailable) {
    this(windows, completedWindows, weeklyNextAvailable, false);
  }
}
