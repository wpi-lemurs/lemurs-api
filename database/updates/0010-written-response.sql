CREATE TABLE written_response (
  id INT GENERATED ALWAYS AS IDENTITY,
  survey_response_id INT NOT NULL,
  written_question_id INT NOT NULL,
  written_data DATE NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  FOREIGN KEY(survey_response_id) REFERENCES survey_response(id),
  PRIMARY KEY (id)
);

-- Indexes for performance
CREATE INDEX idx_written_response_survey_id ON written_response(survey_response_id);
CREATE INDEX idx_written_response_question_id ON written_response(written_question_id);
CREATE INDEX idx_written_response_timestamp ON written_response(timestamp);
CREATE INDEX idx_written_response_created_at ON written_response(created_at);

-- Index for querying by user (through survey_response relationship)
CREATE INDEX idx_written_response_user_timestamp ON written_response(survey_response_id, timestamp);