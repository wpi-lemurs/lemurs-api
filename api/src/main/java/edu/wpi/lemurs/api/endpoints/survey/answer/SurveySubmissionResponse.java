/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

/**
 * Response DTO for survey submissions. Returns the survey response ID that can be used to link
 * related data (writing, audio, etc.).
 */
public class SurveySubmissionResponse {

  /**
   * The ID of the primary survey response created from this submission. This ID should be used to
   * link writing and audio responses to this survey.
   */
  private Integer surveyResponseId;

  /** Optional success message. */
  private String message;

  /** Default constructor. */
  public SurveySubmissionResponse() {}

  /** Constructor for successful submissions with just the ID. */
  public SurveySubmissionResponse(Integer surveyResponseId) {
    this.surveyResponseId = surveyResponseId;
    this.message = "Survey submitted successfully";
  }

  /** All args constructor. */
  public SurveySubmissionResponse(Integer surveyResponseId, String message) {
    this.surveyResponseId = surveyResponseId;
    this.message = message;
  }

  // Getters
  public Integer getSurveyResponseId() {
    return surveyResponseId;
  }

  public String getMessage() {
    return message;
  }

  // Setters
  public void setSurveyResponseId(Integer surveyResponseId) {
    this.surveyResponseId = surveyResponseId;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
