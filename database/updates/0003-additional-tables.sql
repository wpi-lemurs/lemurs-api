-- Table for capturing screentime response.
CREATE TABLE screentime (
	id INT GENERATED ALWAYS AS IDENTITY,
	app_user_id INT NOT NULL, 
	timestamp TIMESTAMP NOT NULL,
	start_time TIMESTAMP,
  end_time TIMESTAMP,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	PRIMARY KEY (id)
);

-- Table for capturing screentime data on each app.
CREATE TABLE screentime_app (
	id INT GENERATED ALWAYS AS IDENTITY,
	screentime_id INT NOT NULL,
	app_name VARCHAR NOT NULL,
  total_time_ms INT NOT NULL,
  last_time_used TIMESTAMP NOT NULL,
	FOREIGN KEY(screentime_id) REFERENCES screentime(id),
	PRIMARY KEY (id)
);

/*
Outdated & unnecessary table. Can introduce similar & improved functionality with a view.

-- Table for combined data for a single session.
CREATE TABLE combined_data (
	app_user_id INT NOT NULL, 
	day DATE NOT NULL,
	session_name VARCHAR NOT NULL,
	survey_response_id INT NOT NULL,
	audio_id INT NOT NULL,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
  FOREIGN KEY(survey_response_id) REFERENCES survey_response(id),
  FOREIGN KEY(audio_id) REFERENCES audio(id),
	PRIMARY KEY (app_user_id, day, session_name)
);
 */