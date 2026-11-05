ALTER TABLE child_event_attendance
ADD CONSTRAINT unique_child_per_event UNIQUE (child_id, event_id);