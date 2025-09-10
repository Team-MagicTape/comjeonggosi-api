-- User category scores for tracking learning progress per category
CREATE TABLE IF NOT EXISTS user_category_scores
(
    id          BIGSERIAL PRIMARY KEY,
    profile_id  BIGINT           NOT NULL REFERENCES user_learning_profiles (id) ON DELETE CASCADE,
    category_id BIGINT           NOT NULL REFERENCES categories (id),
    score       DOUBLE PRECISION NOT NULL DEFAULT 0.5,
    created_at  TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (profile_id, category_id)
);

CREATE INDEX idx_user_category_scores_profile_id ON user_category_scores (profile_id);
CREATE INDEX idx_user_category_scores_category_id ON user_category_scores (category_id);