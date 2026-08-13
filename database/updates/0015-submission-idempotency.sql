-- Make survey submission idempotent.
--
-- Numbered 0015 rather than 0013 on purpose. 0013 and 0014 are claimed by the
-- danger-alert re-indexing work on DangerAlertTriggerEndpointRefactor, which has
-- been applied to the dev database (its schema.version reads 0014) even though
-- the files are not in main. update-schema.sh only runs a file when its index is
-- greater than the recorded version, so anything numbered 0013 or 0014 would be
-- skipped on dev silently -- no error, no column, and an app sending
-- client_submission_id to a database that cannot store it.
--
-- A submission had no identity of its own: recordAnswers always inserted a new
-- survey_response row, so a double-tap, a network retry, or a background sync
-- replay each produced a full duplicate set. The duplicate_survey_responses view
-- exists to find these after the fact; this stops them being created.
--
-- The client now sends a clientSubmissionId that is stable across retries of the
-- same attempt, and the server refuses to store the same one twice.

-- 1. Nullable, because every row written before this migration has no id, and
--    the study's existing data is deliberately left untouched.
ALTER TABLE "survey_response"
    ADD COLUMN "client_submission_id" VARCHAR(64);

-- 2. Scoped to the user, because two participants could in principle generate
--    the same id and one must not block the other's submission.
--
--    survey_id is included because one submission may carry several surveys,
--    which legitimately share an id. In the current data every submission is a
--    single survey (2146 of 2146 real submissions), but the payload has always
--    allowed a list and this keeps that open.
--
--    Partial, so the historical rows with a NULL id are exempt. In Postgres
--    every NULL is already distinct; the WHERE clause states the intent.
CREATE UNIQUE INDEX "survey_response_client_submission_id_key"
    ON "survey_response" ("app_user_id", "client_submission_id", "survey_id")
    WHERE "client_submission_id" IS NOT NULL;

-- 3. The lookup on the write path filters by (app_user_id, client_submission_id),
--    a prefix of the index above, so no additional index is needed.
