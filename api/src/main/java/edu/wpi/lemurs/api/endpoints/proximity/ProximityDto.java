/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.proximity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProximityDto {
  private LocalDateTime timestamp;
  private Integer numberOfDevices;
}
