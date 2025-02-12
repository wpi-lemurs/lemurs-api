/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.test.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link TestEmailDto} represents a test email to be sent. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestEmailDto {
  private String to;
}
