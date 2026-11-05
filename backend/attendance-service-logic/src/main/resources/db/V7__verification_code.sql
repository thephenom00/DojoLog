CREATE TABLE verification_code (
     id BIGSERIAL PRIMARY KEY,
     code VARCHAR(255),
     created_date TIMESTAMP,
     user_id BIGINT,
     CONSTRAINT fk_verification_code_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
