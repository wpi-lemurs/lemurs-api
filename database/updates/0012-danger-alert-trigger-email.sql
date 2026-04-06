-- 1. Add the new column with default false
ALTER TABLE "danger_alert_trigger"
    ADD COLUMN "send_email" BOOLEAN NOT NULL DEFAULT FALSE;

-- 2. Update specific rows to enable email sending
UPDATE "danger_alert_trigger"
SET "send_email" = TRUE
WHERE "question_id" IN (5, 8, 13);