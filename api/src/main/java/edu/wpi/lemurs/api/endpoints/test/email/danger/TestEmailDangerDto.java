/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.test.email.danger;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link TestEmailDangerDto} represents a test email to be sent. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestEmailDangerDto {
  private Integer userID;
  private List<String> reasons;
}
