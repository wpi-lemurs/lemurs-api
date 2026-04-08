/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "danger_alert_trigger")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DangerAlertTrigger {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "question_id", nullable = false)
  private Integer questionId;

  @Column(name = "threshold", nullable = false)
  private Integer threshold;

  @Column(name = "alert_message", nullable = false)
  private String alertMessage;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Column(name = "created_at", insertable = false, updatable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", insertable = false)
  private ZonedDateTime updatedAt;

  @Column(name = "send_email", nullable = false)
  private Boolean sendEmail;
}
