-- Table for users.
CREATE TABLE app_user (
	id INT GENERATED ALWAYS AS IDENTITY,
	is_disabled BOOLEAN NOT NULL,
	is_deleted BOOLEAN NOT NULL,
	PRIMARY KEY (id)
);

-- Table for umass_ids.
CREATE TABLE umass_id (
	app_user_id INT NOT NULL,
	umass_id VARCHAR NOT NULL UNIQUE,
	PRIMARY KEY (app_user_id),
  FOREIGN KEY(app_user_id) REFERENCES app_user(id)
);

-- Index for finding users based on their umass id. (Recorded for any user with data permissions.)
CREATE INDEX idx_umass_id 
ON umass_id (umass_id);

-- Table for personal information. (Not recorded unless the user has elevated permissions.)
CREATE TABLE user_info (
	app_user_id INT NOT NULL,
	email VARCHAR NOT NULL UNIQUE,
	first_name VARCHAR,
	last_name VARCHAR,
	PRIMARY KEY (app_user_id),
  FOREIGN KEY(app_user_id) REFERENCES app_user(id)
);

-- Index for finding users based on their email.
CREATE INDEX idx_email 
ON user_info (email);

-- Table for microsoft authentication.
CREATE TABLE auth_microsoft (
	auth_id VARCHAR NOT NULL,
	app_user_id INT NOT NULL UNIQUE,
	updated TIMESTAMP,
	PRIMARY KEY (app_user_id),
  FOREIGN KEY(app_user_id) REFERENCES app_user(id)
);

-- Table for authorized emails. (This email will not be stored with the user account.)
CREATE TABLE authorized_email (
	email VARCHAR NOT NULL,
	umass_id VARCHAR NOT NULL,
	expiration TIMESTAMP,
	PRIMARY KEY (email)
);

-- Table for authorized emails with elevated permission. (This email will be stored with the user account.)
CREATE TABLE authorized_email_elevated (
	email VARCHAR NOT NULL,
	role INT NOT NULL,
	expiration TIMESTAMP,
	PRIMARY KEY (email, role)
);

-- Table for roles.
CREATE TABLE app_role (
	app_user_id INT NOT NULL,
	role INT NOT NULL,
	PRIMARY KEY (app_user_id, role),
  FOREIGN KEY(app_user_id) REFERENCES app_user(id)
);

-- Table for generic data -- allows for quick updates on app side.
CREATE TABLE data (
	id INT GENERATED ALWAYS AS IDENTITY,
	type VARCHAR(255) NOT NULL,
  data JSONB NOT NULL,
  status INT NOT NULL,
	PRIMARY KEY (id)
);

-- Table for emails of people who should be alerted if someone is in danger.
CREATE TABLE danger_alert_email (
	email VARCHAR,
	PRIMARY KEY (email)
);

INSERT INTO "danger_alert_email" ("email") OVERRIDING SYSTEM VALUE VALUES
    ('khickey@wpi.edu'),
    ('rundenst@wpi.edu'),
    ('cmaerker@umass.edu'),
    ('katiedg@umass.edu');
