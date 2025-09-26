/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entity representing an audio response from a weekly survey session. This entity is linked
 * directly to a survey_response record.
 */
@Table(name = "audio_response")
@Entity
public class AudioResponse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(name = "survey_response_id", nullable = false)
  private Integer surveyResponseId;

  @Column(name = "audio_question_id", nullable = false)
  private Integer audioQuestionId;

  @Column(name = "audio_data", nullable = false)
  private byte[] audioData;

  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  // Default constructor
  public AudioResponse() {}

  // Constructor with essential fields
  public AudioResponse(
      Integer surveyResponseId,
      Integer audioQuestionId,
      byte[] audioData,
      LocalDateTime timestamp) {
    this.surveyResponseId = surveyResponseId;
    this.audioQuestionId = audioQuestionId;
    this.audioData = audioData;
    this.timestamp = timestamp;
  }

  // All args constructor
  public AudioResponse(
      Integer id,
      Integer surveyResponseId,
      Integer audioQuestionId,
      byte[] audioData,
      LocalDateTime timestamp,
      LocalDateTime createdAt) {
    this.id = id;
    this.surveyResponseId = surveyResponseId;
    this.audioQuestionId = audioQuestionId;
    this.audioData = audioData;
    this.timestamp = timestamp;
    this.createdAt = createdAt;
  }

  // Getters
  public Integer getId() {
    return id;
  }

  public Integer getSurveyResponseId() {
    return surveyResponseId;
  }

  public Integer getAudioQuestionId() {
    return audioQuestionId;
  }

  public byte[] getAudioData() {
    return audioData;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  // Setters
  public void setId(Integer id) {
    this.id = id;
  }

  public void setSurveyResponseId(Integer surveyResponseId) {
    this.surveyResponseId = surveyResponseId;
  }

  public void setAudioQuestionId(Integer audioQuestionId) {
    this.audioQuestionId = audioQuestionId;
  }

  public void setAudioData(byte[] audioData) {
    this.audioData = audioData;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /** Gets the size of the audio data in bytes. */
  public int getAudioDataSize() {
    return audioData != null ? audioData.length : 0;
  }
}
