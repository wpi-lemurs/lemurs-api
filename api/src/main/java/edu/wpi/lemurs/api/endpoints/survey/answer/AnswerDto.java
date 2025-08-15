/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnswerDto {
    @JsonProperty("questionId")
    @JsonAlias({"id", "question_id"})
    private Integer questionId;
    
    @JsonProperty("answer")
    @JsonAlias({"response", "value"})
    private String answer;
}