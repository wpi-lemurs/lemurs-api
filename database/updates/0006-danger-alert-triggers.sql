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
VALUES
  (2, 2, 'Expressed a desire to die (score: {score}/5 on question ''I wanted to die'').', TRUE),
  (3, 2, 'Thoughts of taking own life (score: {score}/5 on question ''I thought about taking my life'').', TRUE),
  (5, 2, 'Suicide preparation (score: {score}/5 on question ''Considered a specific suicide method or made preparations for your death'').', TRUE),
  (7, 2, 'Intense desire to kill oneself (score: {score}/5 on question ''How intense was your desire to kill yourself?'').', TRUE),
  (8, 2, 'Intent to kill oneself right now (score: {score}/1 on question ''Do you intend to kill yourself right now?'').', TRUE),
  (9, 2, 'Urge to injure self without intent to die (score: {score}/5 on question ''How strong was your urge to injure yourself without any intent to die?'').', TRUE),
  (11, 2, 'Injured self without intent to die (score: {score}/1 on question ''Injured yourself without any intent to die'').', TRUE),
  (12, 2, 'Attempted suicide (score: {score}/1 on question ''Have you attempted suicide?'').', TRUE),
  (13, 2, 'Severe injury or medical intervention required (score: {score}/1 on question ''Were you severely injured or did you require medical intervention?'').', TRUE);
-- Create index for faster lookups
CREATE INDEX ON "danger_alert_trigger" ("question_id");
