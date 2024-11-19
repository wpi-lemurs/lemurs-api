-- Table for users.
CREATE TABLE app_user (
	id INT GENERATED ALWAYS AS IDENTITY,
	umass_id VARCHAR NOT NULL UNIQUE,
	is_disabled BOOLEAN NOT NULL,
	is_deleted BOOLEAN NOT NULL,
	PRIMARY KEY (id)
);

-- Index for finding users based on their umass id.
CREATE INDEX idx_app_user_umass_id 
ON app_user (umass_id);

-- Table for microsoft authentication.
CREATE TABLE auth_microsoft (
	auth_id VARCHAR NOT NULL,
	app_user_id INT NOT NULL UNIQUE,
	updated TIMESTAMP,
	PRIMARY KEY (app_user_id),
	CONSTRAINT fk_app_auth_microsoft_app_user_id
      FOREIGN KEY(app_user_id) 
        REFERENCES app_user(id)
);

-- Table for authorized emails.
CREATE TABLE authorized_email (
	email VARCHAR NOT NULL,
	umass_id INT NOT NULL,
	expiration TIMESTAMP,
	PRIMARY KEY (email)
);

-- Table for roles.
CREATE TABLE app_role (
	app_user_id INT NOT NULL,
	role INT NOT NULL,
	PRIMARY KEY (app_user_id, role),
	CONSTRAINT fk_app_roles_app_user_id
      FOREIGN KEY(app_user_id) 
        REFERENCES app_user(id)
);

-- Table for generic data.
CREATE TABLE data (
	id INT GENERATED ALWAYS AS IDENTITY,
	type VARCHAR(255) NOT NULL,
  data JSONB NOT NULL,
  status INT NOT NULL,
	PRIMARY KEY (id)
);