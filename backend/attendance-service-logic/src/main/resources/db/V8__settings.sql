CREATE TABLE app_settings (
    id BIGINT PRIMARY KEY,
    enrollment_open BOOLEAN NOT NULL DEFAULT false,
    training_generation BOOLEAN NOT NULL DEFAULT false
);

INSERT INTO app_settings (id, enrollment_open, training_generation) VALUES (1, false, false);
