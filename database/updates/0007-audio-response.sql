-- Migration for audio response table
-- This table stores audio responses from weekly surveys, linked directly to survey_response records

-- Table for audio responses (links directly to survey_response)
CREATE TABLE audio_response (
    id INT GENERATED ALWAYS AS IDENTITY,
    survey_response_id INT NOT NULL,
    audio_question_id INT NOT NULL,
    audio_data BYTEA NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY(survey_response_id) REFERENCES survey_response(id),
    PRIMARY KEY (id)
);

-- Indexes for performance
CREATE INDEX idx_audio_response_survey_id ON audio_response(survey_response_id);
CREATE INDEX idx_audio_response_question_id ON audio_response(audio_question_id);
CREATE INDEX idx_audio_response_timestamp ON audio_response(timestamp);
CREATE INDEX idx_audio_response_created_at ON audio_response(created_at);

-- Index for querying by user (through survey_response relationship)
CREATE INDEX idx_audio_response_user_timestamp ON audio_response(survey_response_id, timestamp);
