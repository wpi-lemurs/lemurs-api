-- Assigns each survey type (morning, afternoon, phq-9, etc.) an integer ID
CREATE TABLE survey (
	id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

-- Lists all survey questions
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

-- Bridge table view based on survey id.
CREATE VIEW survey_question_view AS
	SELECT S.id AS survey_id, Q.*, B.position AS position
	FROM survey S JOIN survey_question B ON S.id = B.survey_id JOIN question Q ON B.question_id = Q.id;

-- Unique instances for each time a user completes a survey type
CREATE TABLE survey_response (
	id INT GENERATED ALWAYS AS IDENTITY,
	app_user_id INT NOT NULL, 
	survey_id INT NOT NULL,
	timestamp TIMESTAMP NOT NULL,
	notification_start TIMESTAMP,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	FOREIGN KEY(survey_id) REFERENCES survey(id),
	PRIMARY KEY (id)
);

-- Captures answer and ties it to corresponding question and survey_response
CREATE TABLE answer (
	id INT GENERATED ALWAYS AS IDENTITY,
	survey_response_id INT NOT NULL,
	question_id INT NOT NULL,
	answer TEXT,
	FOREIGN KEY(question_id) REFERENCES question(id),
	FOREIGN KEY(survey_response_id) REFERENCES survey_response(id),
	PRIMARY KEY (id)
);

-- User overall progress
CREATE TABLE progress (
	app_user_id INT NOT NULL,
	earned DECIMAL(8, 2) NOT NULL,
	daily_surveys_completed INT NOT NULL,
	weekly_surveys_completed INT NOT NULL,
	started TIMESTAMP NOT NULL,
	next_daily_survey TIMESTAMP NOT NULL,
	next_weekly_survey TIMESTAMP NOT NULL,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	PRIMARY KEY (app_user_id)
);

-- List the earnings for different surveys
CREATE TABLE incentive (
	id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL UNIQUE,
	reward DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (id)
);

-- Lists the goals, how they are attained, and their reward
CREATE TABLE goal (
	id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL,
	required_daily_surveys INT NOT NULL,
	max_days INT NULL,
	reward DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (id)
);

-- Connects users to their goal progress
CREATE TABLE goal_progress (
	app_user_id INT NOT NULL,
	goal_id INT NOT NULL,
	is_complete BOOLEAN NULL DEFAULT NULL,
	time_limit TIMESTAMP NOT NULL,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	FOREIGN KEY(goal_id) REFERENCES goal(id),
	PRIMARY KEY (app_user_id, goal_id)
);

-- Table for survey opening times. 
-- (Could, in the very distant future, be modified to be per date, or even per user.)
CREATE TABLE survey_availability (
	name VARCHAR NOT NULL,
	open_time TIME NOT NULL,
	close_time TIME NOT NULL,
	PRIMARY KEY (name)
);