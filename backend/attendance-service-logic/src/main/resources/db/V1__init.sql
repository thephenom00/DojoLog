
-- Table: users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    hourly_rate INTEGER,
    role VARCHAR(255) NOT NULL,
    dtype VARCHAR(31) NOT NULL
);

-- Table: school
CREATE TABLE school (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    contact_person VARCHAR(255),
    contact_number VARCHAR(255),
    instructions TEXT
);

-- Table: training
CREATE TABLE training (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    day_of_week VARCHAR(255),
    start_time TIME,
    end_time TIME,
    price INTEGER,
    capacity INTEGER,
    school_id INTEGER REFERENCES school(id)
);

-- Table: training_upcoming_dates
CREATE TABLE training_upcoming_dates (
    training_id INTEGER REFERENCES training(id),
    training_upcoming_date DATE
);

-- Table: child
CREATE TABLE child (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    date_of_birth DATE,
    street VARCHAR(255),
    city VARCHAR(255),
    zip INTEGER,
    birth_number VARCHAR(255),
    requested_training_id BIGINT,
    training_id INTEGER REFERENCES training(id),
    parent_id INTEGER REFERENCES users(id)
);

-- Table: child_attendance
CREATE TABLE child_attendance (
    id SERIAL PRIMARY KEY,
    child_id INTEGER REFERENCES child(id),
    training_unit_id INTEGER,
    present BOOLEAN
);

-- Table: child_event_attendance
CREATE TABLE child_event_attendance (
    id SERIAL PRIMARY KEY,
    child_id INTEGER REFERENCES child(id),
    event_id INTEGER,
    present BOOLEAN,
    note TEXT,
    payment_received BOOLEAN
);

-- Table: event
CREATE TABLE event (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    location VARCHAR(255),
    start_time TIME,
    end_time TIME,
    places INTEGER,
    price INTEGER,
    description TEXT,
    trainer_salary INTEGER,
    event_type VARCHAR(255)
);

-- Table: holiday
CREATE TABLE holiday (
    id SERIAL PRIMARY KEY,
    school_id INTEGER REFERENCES school(id),
    name VARCHAR(255),
    start_date DATE,
    end_date DATE
);

-- Table: news
CREATE TABLE news (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    date DATE,
    description VARCHAR(255)
);

-- Table: training_unit
CREATE TABLE training_unit (
    id SERIAL PRIMARY KEY,
    training_id INTEGER REFERENCES training(id),
    date DATE,
    description VARCHAR(255),
    current BOOLEAN
);

-- Table: trainer_attendance
CREATE TABLE trainer_attendance (
    id SERIAL PRIMARY KEY,
    trainer_id INTEGER REFERENCES users(id),
    training_unit_id INTEGER REFERENCES training_unit(id),
    present BOOLEAN
);

-- Table: trainer_report
CREATE TABLE trainer_report (
    id SERIAL PRIMARY KEY,
    trainer_id BIGINT,
    trainer_attendance_id BIGINT,
    date VARCHAR(255),
    name VARCHAR(255),
    day_of_week VARCHAR(255),
    start_time TIME,
    end_time TIME,
    hours DOUBLE PRECISION,
    type VARCHAR(255),
    money INTEGER
);

-- Table: trainers_in_training
CREATE TABLE trainers_in_training (
    training_id INTEGER REFERENCES training(id),
    trainer_id INTEGER REFERENCES users(id)
);

-- Table: trainers_in_events
CREATE TABLE trainers_in_events (
    trainer_id INTEGER REFERENCES users(id),
    event_id INTEGER REFERENCES event(id)
);

-- Table: trainer_substitution
CREATE TABLE trainer_substitution (
    id SERIAL PRIMARY KEY,
    original_trainer_id BIGINT,
    substitute_trainer_id BIGINT,
    date DATE,
    training_id INTEGER REFERENCES training(id)
);
