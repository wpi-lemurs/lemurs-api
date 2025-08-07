/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

public class DangerAlertTriggerDto {
  private Integer id;
  private Integer questionId;
  private Integer threshold;
  private String alertMessage;
  private Boolean isActive;

  // Constructors
  public DangerAlertTriggerDto() {}

  public DangerAlertTriggerDto(
      Integer id, Integer questionId, Integer threshold, String alertMessage, Boolean isActive) {
    this.id = id;
    this.questionId = questionId;
    this.threshold = threshold;
    this.alertMessage = alertMessage;
    this.isActive = isActive;
  }

  // Getters and setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Integer questionId) {
    this.questionId = questionId;
  }

  public Integer getThreshold() {
    return threshold;
  }

  public void setThreshold(Integer threshold) {
    this.threshold = threshold;
  }

  public String getAlertMessage() {
    return alertMessage;
  }

  public void setAlertMessage(String alertMessage) {
    this.alertMessage = alertMessage;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }
}
