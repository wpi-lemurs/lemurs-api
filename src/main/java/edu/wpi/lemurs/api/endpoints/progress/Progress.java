/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link Progress} represents a user's progress. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Progress {
  @Id
  @Column(nullable = false, name = "app_user_id")
  private Integer userID;

  @Column(nullable = false)
  private BigDecimal earned;

  @Column(nullable = false)
  private Integer dailySurveysCompleted;

  @Column(nullable = false)
  private Integer weeklySurveysCompleted;

  @Column(nullable = false)
  private Date started;

  @Column(nullable = false)
  private Date nextDailySurvey;

  @Column(nullable = false)
  private Date nextWeeklySurvey;
}
