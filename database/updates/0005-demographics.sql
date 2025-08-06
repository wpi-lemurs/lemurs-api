-- Table for capturing demographic responses.
CREATE TABLE demographic (
	app_user_id INT NOT NULL, 
	keyword VARCHAR NOT NULL,
	value VARCHAR NOT NULL,
  FOREIGN KEY(app_user_id) REFERENCES app_user(id),
	PRIMARY KEY (app_user_id, keyword)
);
