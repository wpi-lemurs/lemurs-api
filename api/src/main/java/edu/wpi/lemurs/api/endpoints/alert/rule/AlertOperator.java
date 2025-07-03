/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.rule;

import lombok.Getter;

/** The operator for an {@link AlertRule}. */
@Getter
public enum AlertOperator {
  GREATER_THAN(">"),
  GREATER_THAN_OR_EQUAL_TO(">="),
  LESS_THAN("<"),
  LESS_THAN_OR_EQUAL_TO("<="),
  EQUAL_TO("=="),
  NOT_EQUAL_TO("!=");

  private final String symbol;

  AlertOperator(String symbol) {
    this.symbol = symbol;
  }

  /**
   * Evaluates the operator against an answer and a threshold.
   *
   * @param answerValue The integer value of the answer.
   * @param thresholdValue The integer value of the threshold.
   * @return True if the condition is met, false otherwise.
   */
  public boolean evaluate(int answerValue, int thresholdValue) {
    switch (this) {
      case GREATER_THAN:
        return answerValue > thresholdValue;
      case GREATER_THAN_OR_EQUAL_TO:
        return answerValue >= thresholdValue;
      case LESS_THAN:
        return answerValue < thresholdValue;
      case LESS_THAN_OR_EQUAL_TO:
        return answerValue <= thresholdValue;
      case EQUAL_TO:
        return answerValue == thresholdValue;
      case NOT_EQUAL_TO:
        return answerValue != thresholdValue;
      default:
        return false;
    }
  }
}