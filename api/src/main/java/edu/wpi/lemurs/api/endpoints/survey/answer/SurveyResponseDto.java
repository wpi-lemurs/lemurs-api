/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SurveyResponseDto {
    @JsonProperty("id")
    @JsonAlias({"surveyId", "survey_id"})
    private Long id;
    
    @JsonProperty("answers")
    @JsonAlias({"responses"})
    private List<AnswerDto> answers;
}