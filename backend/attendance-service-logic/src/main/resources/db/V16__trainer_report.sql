CREATE TABLE IF NOT EXISTS trainer_monthly_report (
    id          SERIAL PRIMARY KEY,
    trainer_id  INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    year        INTEGER NOT NULL,
    month       INTEGER NOT NULL CHECK (month >= 1 AND month <= 12),
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note        TEXT,
    approved_at TIMESTAMP,
    CONSTRAINT uq_trainer_year_month UNIQUE (trainer_id, year, month)
);

CREATE TABLE IF NOT EXISTS trainer_report (
    id                        SERIAL PRIMARY KEY,
    trainer_monthly_report_id INTEGER NOT NULL REFERENCES trainer_monthly_report(id) ON DELETE CASCADE,
    start_date                DATE NOT NULL,
    end_date                  DATE,
    name                      VARCHAR(255) NOT NULL,
    day_of_week               VARCHAR(20),
    report_category           VARCHAR(50) NOT NULL,
    source_id                 INT,
    start_time                TIME,
    end_time                  TIME,
    money                     INTEGER NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS training_schedule_upcoming_dates;