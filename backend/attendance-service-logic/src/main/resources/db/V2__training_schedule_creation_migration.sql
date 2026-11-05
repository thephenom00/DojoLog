-- Create table training_schedule
CREATE TABLE training_schedule (
    id SERIAL PRIMARY KEY,
    training_id INTEGER REFERENCES training(id) ON DELETE CASCADE,
    day_of_week VARCHAR(255) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

-- Migrate data from training
INSERT INTO training_schedule (training_id, day_of_week, start_time, end_time)
SELECT id, day_of_week, start_time, end_time
FROM training
WHERE day_of_week IS NOT NULL
  AND start_time IS NOT NULL
  AND end_time IS NOT NULL;

ALTER TABLE training DROP COLUMN day_of_week;
ALTER TABLE training DROP COLUMN start_time;
ALTER TABLE training DROP COLUMN end_time;

-- Link training_schedule with training_unit
ALTER TABLE training_unit
ADD COLUMN training_schedule_id INTEGER REFERENCES training_schedule(id) ON DELETE CASCADE;

ALTER TABLE training_unit
DROP COLUMN training_id;

