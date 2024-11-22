/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.danger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A {@link DangerAlertEmail} represents an email address that should be alerted if a student is in
 * danger.
 */
@Table(name = "danger_alert_email")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DangerAlertEmail {
  @Id
  @Column(nullable = false)
  private String email;
}
