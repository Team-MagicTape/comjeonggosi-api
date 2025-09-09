CREATE TABLE IF NOT EXISTS user_learning_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_solved INTEGER NOT NULL DEFAULT 0,
    total_correct INTEGER NOT NULL DEFAULT 0,
    streak_days INTEGER NOT NULL DEFAULT 0,
    last_study_date TIMESTAMP,
    preferred_study_time VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_learning_profiles_user_id ON user_learning_profiles(user_id);

CREATE TABLE IF NOT EXISTS user_category_scores (
    profile_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (profile_id, category_id),
    FOREIGN KEY (profile_id) REFERENCES user_learning_profiles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_difficulty_preferences (
    profile_id BIGINT NOT NULL,
    difficulty INTEGER NOT NULL,
    preference DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (profile_id, difficulty),
    FOREIGN KEY (profile_id) REFERENCES user_learning_profiles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_review_schedules (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id VARCHAR(255) NOT NULL,
    review_count INTEGER NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMP,
    next_review_at TIMESTAMP NOT NULL,
    retention_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    difficulty_adjustment DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    is_mastered BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, quiz_id)
);

CREATE INDEX idx_quiz_review_schedules_user_id ON quiz_review_schedules(user_id);
CREATE INDEX idx_quiz_review_schedules_next_review_at ON quiz_review_schedules(next_review_at);
CREATE INDEX idx_quiz_review_schedules_user_quiz ON quiz_review_schedules(user_id, quiz_id);