/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CombinedSurveyResponseDto {
    @JsonProperty("timestamp")
    @JsonAlias({"time", "submissionTime"})
    private Instant timestamp;
    
    @JsonProperty("surveys")
    @JsonAlias({"responses", "surveyResponses"})
    private List<SurveyResponseDto> surveys;
    
    @JsonProperty("notificationStart")
    @JsonAlias({"notification_start", "notificationTime"})
    private Instant notificationStart;
}