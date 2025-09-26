CREATE TABLE step (
    id INT GENERATED ALWAYS AS IDENTITY,
    app_user_id INT NOT NULL,
    steps BIGINT NOT NULL,
    start_timestamp TIMESTAMP NOT NULL,
    end_timestamp TIMESTAMP NOT NULL,
    app_source VARCHAR(255) NOT NULL,
    recorded_date TIMESTAMP NOT NULL,
    type VARCHAR(255) NOT NULL,
    additional_data JSON NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (app_user_id) REFERENCES app_user(id),
    PRIMARY KEY (id)
);

CREATE TABLE distance (
    id INT GENERATED ALWAYS AS IDENTITY,
    app_user_id INT NOT NULL,
    distance BIGINT NOT NULL,
    start_timestamp TIMESTAMP NOT NULL,
    end_timestamp TIMESTAMP NOT NULL,
    app_source VARCHAR(255) NOT NULL,
    recorded_date TIMESTAMP NOT NULL,
    type VARCHAR(255) NOT NULL,
    additional_data JSON NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (app_user_id) REFERENCES app_user(id),
    PRIMARY KEY (id)
);

CREATE TABLE calories (
   id INT GENERATED ALWAYS AS IDENTITY,
   app_user_id INT NOT NULL,
   calories BIGINT NOT NULL,
   start_timestamp TIMESTAMP NOT NULL,
   end_timestamp TIMESTAMP NOT NULL,
   app_source VARCHAR(255) NOT NULL,
   recorded_date TIMESTAMP NOT NULL,
   type VARCHAR(255) NOT NULL,
   additional_data JSON NOT NULL,
   status VARCHAR(255) NOT NULL,
   FOREIGN KEY (app_user_id) REFERENCES app_user(id),
   PRIMARY KEY (id)
);

CREATE TABLE speed (
   id INT GENERATED ALWAYS AS IDENTITY,
   app_user_id INT NOT NULL,
   speed DOUBLE PRECISION[] NOT NULL,
   start_timestamp TIMESTAMP NOT NULL,
   end_timestamp TIMESTAMP NOT NULL,
   unit VARCHAR(50) DEFAULT 'm/s' NOT NULL,
   app_source VARCHAR(255) NOT NULL,
   recorded_date TIMESTAMP NOT NULL,
   type VARCHAR(255) NOT NULL,
   additional_data JSON NOT NULL,
   status VARCHAR(255) NOT NULL,
   FOREIGN KEY (app_user_id) REFERENCES app_user(id),
   PRIMARY KEY (id)
);

-- STEP table
CREATE INDEX idx_step_user_date 
    ON step (app_user_id, recorded_date DESC);

CREATE INDEX idx_step_user_time 
    ON step (app_user_id, start_timestamp, end_timestamp);

CREATE INDEX idx_step_status_type 
    ON step (status, type);

-- DISTANCE table
CREATE INDEX idx_distance_user_date 
    ON distance (app_user_id, recorded_date DESC);

CREATE INDEX idx_distance_user_time 
    ON distance (app_user_id, start_timestamp, end_timestamp);

CREATE INDEX idx_distance_status_type 
    ON distance (status, type);

-- CALORIES table
CREATE INDEX idx_calories_user_date 
    ON calories (app_user_id, recorded_date DESC);

CREATE INDEX idx_calories_user_time 
    ON calories (app_user_id, start_timestamp, end_timestamp);

CREATE INDEX idx_calories_status_type 
    ON calories (status, type);

-- SPEED table
CREATE INDEX idx_speed_user_date 
    ON speed (app_user_id, recorded_date DESC);

CREATE INDEX idx_speed_user_time 
    ON speed (app_user_id, start_timestamp, end_timestamp);

CREATE INDEX idx_speed_status_type 
    ON speed (status, type);
