CREATE TABLE proximity (
   id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
   app_user_id int4 NOT NULL,
   number_of_devices int4 NOT NULL,
   "timestamp" timestamp NOT NULL,
   CONSTRAINT proximity_pk PRIMARY KEY (id),
   CONSTRAINT proximity_app_user_fk FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);

CREATE INDEX idx_proximity_user_devices
    ON proximity (app_user_id, number_of_devices DESC);
