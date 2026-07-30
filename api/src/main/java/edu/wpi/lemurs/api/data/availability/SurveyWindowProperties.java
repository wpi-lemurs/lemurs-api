/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.data.availability;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Maps each survey window to the survey it contains.
 *
 * <p>This lives in configuration rather than the database only until the {@code
 * survey_availability} table gains its own {@code survey_id} column. Once it does, this class
 * should be deleted and the mapping read from the row alongside the open and close times.
 */
@Component
@ConfigurationProperties(prefix = "lemurs.survey")
@Getter
@Setter
public class SurveyWindowProperties {

  /** Window name to survey id, e.g. {@code morning -> 0}. */
  private Map<String, Integer> windowSurveyIds = new HashMap<>();
}
