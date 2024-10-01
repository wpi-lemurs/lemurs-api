/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link LoginDto} represents an attempt to login. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
  // TODO: This is just for testing the JWT.  Currently just logs in as the request user id.
  Integer userID;
}
