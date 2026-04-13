UPDATE "answer" a
SET "answer" = (a."answer"::integer + 1)::text
FROM "question" q
WHERE a."question_id" = q."id"
  AND q."style" ILIKE '%scale%'
  AND a."answer" ~ '^[0-9]+$';
