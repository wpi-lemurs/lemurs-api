/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "danger_alert_trigger")
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

  public DangerAlertTrigger() {}

  public DangerAlertTrigger(Integer id, Integer questionId, Integer threshold, String alertMessage, Boolean isActive) {
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

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public ZonedDateTime getUpdatedAt() {
    return updatedAt;
  }
}
