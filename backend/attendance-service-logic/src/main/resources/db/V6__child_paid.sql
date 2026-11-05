-- add paid variable to the child table
ALTER TABLE child
ADD COLUMN paid BOOLEAN;

UPDATE child
SET paid = FALSE;

ALTER TABLE child
ALTER COLUMN paid SET DEFAULT FALSE;

ALTER TABLE child
ALTER COLUMN paid SET NOT NULL;
