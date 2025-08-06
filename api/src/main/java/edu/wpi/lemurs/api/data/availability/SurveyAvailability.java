/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.data.availability;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link SurveyAvailability} represents survey availability. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SurveyAvailability {
  @Id
  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Time openTime;

  @Column(nullable = false)
  private Time closeTime;
}
