/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** A {@link UserGoal} represents the compounded id. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserGoal {

  @Column(nullable = false, name = "app_user_id")
  private Integer userID;

  @Column(nullable = false, name = "goal_id")
  private Integer goalID;
}
