/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.distance;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DistanceDto {
  private String userId;
  private String type;
  private Long distance;
  private LocalDateTime start_timestamp;
  private LocalDateTime end_timestamp;
  private String appSource;
}
