-- Add table for configurable danger alert triggers
CREATE TABLE IF NOT EXISTS "danger_alert_trigger" (
  "id" SERIAL PRIMARY KEY,
  "question_id" INTEGER NOT NULL REFERENCES "question" ("id"),
  "threshold" INTEGER NOT NULL,
  "alert_message" TEXT NOT NULL,
  "is_active" BOOLEAN NOT NULL DEFAULT TRUE,
  "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Add initial trigger for question ID 2 "I wanted to die"
INSERT INTO "danger_alert_trigger" ("question_id", "threshold", "alert_message", "is_active")
VALUES (2, 3, 'Expressed a desire to die (score: {score}/5 on question ''I wanted to die'').', TRUE);

-- Create index for faster lookups
CREATE INDEX ON "danger_alert_trigger" ("question_id");
