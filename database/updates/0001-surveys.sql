-- Table for surveys.
CREATE TABLE survey (
	id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL,
	is_daily BOOLEAN NOT NULL DEFAULT FALSE,
	is_weekly BOOLEAN NOT NULL DEFAULT FALSE,
	PRIMARY KEY (id)
);

-- Table for questions.
CREATE TABLE question (
	id INT GENERATED ALWAYS AS IDENTITY,
	question TEXT NOT NULL,
	style VARCHAR(255) NOT NULL,
	options TEXT[] NULL,
	parent_question_id INT NULL,
	prerequisite_question_id INT NULL,
	prerequisite_answer TEXT,
	requirements TEXT[] NULL,
	PRIMARY KEY (id),
	FOREIGN KEY(parent_question_id) REFERENCES question(id),
	FOREIGN KEY(prerequisite_question_id) REFERENCES question(id)
);

-- Bridge table between surveys and questions.
CREATE TABLE survey_question (
	survey_id INT NOT NULL,
	question_id INT NOT NULL,
	position INT NOT NULL,
	FOREIGN KEY(survey_id) REFERENCES survey(id),
  FOREIGN KEY(question_id) REFERENCES question(id),
	PRIMARY KEY (survey_id, question_id)
);

CREATE TABLE survey_response (
	id INT NOT NULL,
	app_user_id INT NOT NULL, 
	survey_id INT NOT NULL,
	timestamp TIMESTAMP NOT NULL,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	FOREIGN KEY(survey_id) REFERENCES survey(id),
	PRIMARY KEY (id)
);

CREATE TABLE answer (
	id INT NOT NULL,
	survey_response_id INT NOT NULL,
	answer TEXT,
	FOREIGN KEY(survey_response_id) REFERENCES survey(id),
	PRIMARY KEY (id)
);