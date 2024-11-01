/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.microsoft;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A {@link AuthMicrosoft} represents the connection from a Microsoft account to a application
 * person account.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthMicrosoft {

  @Id
  @Column(nullable = false, name = "auth_id")
  private String authID;

  @Column(nullable = false, name = "app_user_id")
  private Integer userID;
}
