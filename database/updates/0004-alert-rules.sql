-- Table for alert rules.
CREATE TABLE alert_rule (
	id INT GENERATED ALWAYS AS IDENTITY,
	question_id INT NOT NULL,
	operator VARCHAR(255) NOT NULL,
	threshold VARCHAR(255) NOT NULL,
	reason_template TEXT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY(question_id) REFERENCES question(id)
);

-- Index for finding rules based on their question id.
CREATE INDEX idx_alert_rule_question_id
ON alert_rule (question_id);

-- Insert the initial rule for suicidal ideation.
-- Question 10: "How strong was your urge to make a suicide attempt?"
-- Threshold: > 3
INSERT INTO alert_rule (question_id, operator, threshold, reason_template) VALUES
(10, 'GREATER_THAN', '3', 'User answered ''{answer}'' to question ID {questionId} (''How strong was your urge to make a suicide attempt?''). The configured alert rule is (answer {operator} {threshold}).');