/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.authorize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link AuthEmailDto} represents a new user email authorization. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthEmailDto {
  private String umassId;
  private String email;
  // TODO: Allow for adding user with permissions.
}
