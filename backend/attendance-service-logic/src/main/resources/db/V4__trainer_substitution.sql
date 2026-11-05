-- Link substitution with schedule
ALTER TABLE trainer_substitution
ADD COLUMN training_schedule_id INTEGER REFERENCES training_schedule(id);

ALTER TABLE trainer_substitution
DROP COLUMN training_id;
