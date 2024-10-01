-- Table for users.
CREATE TABLE app_user (
	id INT GENERATED ALWAYS AS IDENTITY,
	umass_id INT NOT NULL,
	is_disabled BOOLEAN NOT NULL,
	is_deleted BOOLEAN NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (umass_id)
);

-- Index for finding users based on their umass id.
CREATE INDEX idx_app_user_umass_id 
ON app_user (umass_id);

-- Table for generic data.
CREATE TABLE data (
	id INT GENERATED ALWAYS AS IDENTITY,
	type VARCHAR(255) NOT NULL,
  data JSONB NOT NULL,
  status INT NOT NULL,
	PRIMARY KEY (id)
);