-- Attach upcoming dates to each schedule
DROP TABLE IF EXISTS training_upcoming_dates;
CREATE TABLE training_schedule_upcoming_dates (
    training_schedule_id INTEGER REFERENCES training_schedule(id) ON DELETE CASCADE,
    upcoming_date DATE NOT NULL
);