/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.rule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An {@link AlertRule} defines a condition that, when met, triggers a danger alert. */
@Table(name = "alert_rule")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AlertRule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false)
  private Integer questionId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AlertOperator operator;

  /** The value to compare the answer against. Stored as a string to support non-numeric types. */
  @Column(nullable = false)
  private String threshold;

  /**
   * A template for the reason sent in the alert email. Can use placeholders like {