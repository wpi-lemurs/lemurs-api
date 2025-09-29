package edu.wpi.lemurs.api.endpoints.bluetooth.proximity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Proximity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false, name = "app_user_id")
  private Integer appUserId;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @Column(nullable = false, name = "number_of_devices")
  private Integer numberOfDevices;
}