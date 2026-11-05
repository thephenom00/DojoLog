-- Step 1: Drop the existing FK constraint that links verification_code to users
ALTER TABLE verification_code
DROP CONSTRAINT IF EXISTS fk54hony7tk7x7gaaqp0rf16raa,
DROP CONSTRAINT IF EXISTS fk_verification_code_user;

-- Step 2: Drop the old user_id column (we no longer need the FK)
ALTER TABLE verification_code
DROP COLUMN user_id;

-- Step 3: Add the new email column (plain string, no FK)
ALTER TABLE verification_code
    ADD COLUMN email VARCHAR(255);

-- Step 4: Add the purpose column (FORGOTTEN_PASSWORD or REGISTER)
ALTER TABLE verification_code
    ADD COLUMN purpose VARCHAR(50);
