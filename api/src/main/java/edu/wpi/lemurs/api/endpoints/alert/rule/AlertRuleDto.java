/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A DTO for transferring {@link AlertRule} data. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AlertRuleDto {
  private Integer id;
  private Integer questionId;
  private AlertOperator operator;
  private String threshold;
  private String reasonTemplate;
}