/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.speed;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SpeedDto {
  private String userId;
  private String type = "speed";
  private List<Double> speed;
  private LocalDateTime start_timestamp;
  private LocalDateTime end_timestamp;
  private String unit = "m/s";
  private String appSource;
}
