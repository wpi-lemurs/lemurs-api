/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProgressResponse {
  private BigDecimal earned;
  private Integer dailySurveysTotalCompleted;
  private Integer dailySurveysTotalGoal;
  private BigDecimal dailySurveysTotalBonus;
  private Integer dailySurveysWeeklyCompleted;
  private Integer dailySurveysWeeklyGoal;
  private BigDecimal dailySurveysWeeklyBonus;
  private String nextDailySurvey;
}
