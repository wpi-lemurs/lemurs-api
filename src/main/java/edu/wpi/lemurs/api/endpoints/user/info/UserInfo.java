/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link UserInfo} represents a info on a user. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInfo {
  @Id
  @Column(nullable = false, name = "app_user_id")
  private Integer userId;

  @Column(nullable = false)
  private String email;

  @Column(nullable = true)
  private String firstName;

  @Column(nullable = true)
  private String lastName;
}
