CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    profile_image_url TEXT,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users ADD CONSTRAINT uk_users_provider_provider_id UNIQUE (provider, provider_id);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_provider ON users(provider);