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
	id INT GENERATED ALWAYS AS IDENTITY,
	app_user_id INT NOT NULL, 
	survey_id INT NOT NULL,
	timestamp TIMESTAMP NOT NULL,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	FOREIGN KEY(survey_id) REFERENCES survey(id),
	PRIMARY KEY (id)
);

CREATE TABLE answer (
	id INT GENERATED ALWAYS AS IDENTITY,
	survey_response_id INT NOT NULL,
	answer TEXT,
	FOREIGN KEY(survey_response_id) REFERENCES survey(id),
	PRIMARY KEY (id)
);

CREATE TABLE progress (
	app_user_id INT NOT NULL,
	earned DECIMAL(8, 2) NOT NULL,
	daily_surveys_completed INT NOT NULL,
	weekly_surveys_completed INT NOT NULL,
	week_reset TIMESTAMP NOT NULL,
	next_daily_survey TIMESTAMP NOT NULL,
	next_weekly_survey TIMESTAMP NOT NULL,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	PRIMARY KEY (app_user_id)
);

CREATE TABLE incentive (
	id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL UNIQUE,
	reward DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE goal (
	id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL,
	required_daily_surveys INT NOT NULL,
	reward DECIMAL(8,2) NOT NULL,
	prerequisite_goal_id INT NULL,
	FOREIGN KEY(prerequisite_goal_id) REFERENCES goal(id),
	PRIMARY KEY (id)
);