/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.danger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dto for a {@link DangerAlertEmail}. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DangerAlertEmailDto {
  private String email;
}
