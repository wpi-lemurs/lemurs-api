/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link UserDto} represents a new {@link User}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private Integer umassId;
  private String email;
}
