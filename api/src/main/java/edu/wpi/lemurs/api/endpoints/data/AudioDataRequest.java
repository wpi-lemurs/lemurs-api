/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for audio responses from weekly surveys. This DTO matches the JSON structure
 * sent by the client application.
 */
public class AudioDataRequest {

  /**
   * The timestamp when the audio response was created on the client. Expected format:
   * "2025-09-02T18:46:22.123"
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime timestamp;

  /** The ID of the audio question/prompt that was answered. */
  private Integer audioQuestionId;

  /**
   * The user's audio response encoded as a Base64 string. This will be decoded to binary data
   * before storage.
   */
  private String audioByte64;

  /**
   * The ID of the survey response that this audio data belongs to. This links the audio response to
   * the PHQ-9 survey submission.
   */
  private Integer surveyResponseId;

  // Default constructor
  public AudioDataRequest() {}

  // All args constructor
  public AudioDataRequest(
      LocalDateTime timestamp,
      Integer audioQuestionId,
      String audioByte64,
      Integer surveyResponseId) {
    this.timestamp = timestamp;
    this.audioQuestionId = audioQuestionId;
    this.audioByte64 = audioByte64;
    this.surveyResponseId = surveyResponseId;
  }

  // Getters
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public Integer getAudioQuestionId() {
    return audioQuestionId;
  }

  public String getAudioByte64() {
    return audioByte64;
  }

  public Integer getSurveyResponseId() {
    return surveyResponseId;
  }

  // Setters
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public void setAudioQuestionId(Integer audioQuestionId) {
    this.audioQuestionId = audioQuestionId;
  }

  public void setAudioByte64(String audioByte64) {
    this.audioByte64 = audioByte64;
  }

  public void setSurveyResponseId(Integer surveyResponseId) {
    this.surveyResponseId = surveyResponseId;
  }
}
