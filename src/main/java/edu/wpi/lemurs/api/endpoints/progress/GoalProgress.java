/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link GoalProgress} represents whether a user has completed a goal. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(UserGoal.class)
public class GoalProgress {
  @Id
  @Column(nullable = false, name = "app_user_id")
  private Integer userID;

  @Id
  @Column(nullable = false, name = "goal_id")
  private Integer goalID;

  @Column private boolean isComplete;

  @Column(nullable = false)
  private Date timeLimit;
}
