/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.authorize.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link AuthAdminEmailDto} represents a new user email authorization. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthAdminEmailDto {
  private String email;
  private Integer role;
}
