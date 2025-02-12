/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.microsoft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link MicrosoftLoginDto} represents login credentials. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MicrosoftLoginDto {
  private String accessToken;
}
