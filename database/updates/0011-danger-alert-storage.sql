DROP TABLE IF EXISTS public.danger_alert CASCADE;

CREATE TABLE public.danger_alert (
	threshold_id serial4 NOT NULL,
	answer_id int4 NOT NULL,
	created_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT danger_alert_pkey PRIMARY KEY (threshold_id, answer_id),
	CONSTRAINT danger_alert_threshold_id_fkey FOREIGN KEY (threshold_id) REFERENCES public.danger_alert_trigger(id),
	CONSTRAINT danger_alert_answer_id_fkey FOREIGN KEY (answer_id) REFERENCES public.answer(id)
);

CREATE OR REPLACE FUNCTION check_danger_threshold()
RETURNS TRIGGER AS $$
DECLARE
	threshold_val INT;
	threshold_id INT;
BEGIN
	-- Check if Answer.question_id = ANY question_id in  Danger_alert_trigger. Save the threshold and its ID if it is; exit function if not.
	SELECT CASE WHEN ID>=8 THEN 0 ELSE threshold END, id
	INTO threshold_val, threshold_id
	FROM Danger_alert_trigger
	WHERE question_id = NEW.question_id;

	--If Answer.answer > Danger_alert_trigger.threshold, then add entry to Danger_alert.
	IF FOUND THEN

		IF NEW.answer = 'yes' THEN		
			INSERT INTO Danger_alert(threshold_id, answer_id, created_at)
			VALUES (threshold_id, NEW.id, NOW());

		ELSIF NEW.answer != 'no' THEN
			BEGIN
				IF NEW.answer::int >= threshold_val THEN
					INSERT INTO Danger_alert(threshold_id, answer_id, created_at)
					VALUES (threshold_id, NEW.id, NOW());
				END IF;
			END;
		END IF;

	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER new_danger_alert
AFTER INSERT ON Answer
FOR EACH ROW
EXECUTE FUNCTION check_danger_threshold();


-- public.all_danger_alert source
-- view of relevant information for quickly reviewing danger alerts
CREATE OR REPLACE VIEW public.all_danger_alert
AS SELECT s.app_user_id,
    a.answer,
    q.question,
    d.created_at,
    t.alert_message
   FROM danger_alert d
     JOIN answer a ON a.id = d.answer_id
     JOIN question q ON a.question_id = q.id
     JOIN danger_alert_trigger t ON t.id = d.threshold_id
     JOIN survey_response s ON s.id = a.survey_response_id;
