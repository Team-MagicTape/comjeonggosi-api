-- 사용자 테이블
CREATE TABLE users
(
    id                BIGSERIAL PRIMARY KEY,
    provider          VARCHAR(50)  NOT NULL,
    provider_id       VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    nickname          VARCHAR(100) NOT NULL,
    profile_image_url TEXT,
    last_login_at     TIMESTAMP,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(255) NOT NULL DEFAULT 'USER',
    CONSTRAINT uk_users_provider_provider_id UNIQUE (provider, provider_id)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_provider ON users (provider);

-- 카테고리 테이블
CREATE TABLE categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 아티클 테이블
CREATE TABLE articles
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL,
    author_id   BIGINT       NOT NULL,
    category_id BIGINT       NOT NULL,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_articles_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_articles_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_articles_category ON articles (category_id);

-- 퀴즈 제출 테이블
CREATE TABLE submissions
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    quiz_id      VARCHAR(255) NOT NULL,
    answer       VARCHAR(255) NOT NULL,
    is_corrected BOOLEAN      NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_submissions_user_id ON submissions (user_id);
CREATE INDEX idx_submissions_quiz_id ON submissions (quiz_id);
CREATE INDEX idx_submissions_created_at ON submissions (created_at DESC);

-- 질문 테이블
CREATE TABLE questions
(
    id          BIGSERIAL PRIMARY KEY,
    day         BIGINT       NOT NULL,
    category_id BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL,
    answer      TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT uk_questions_day_category UNIQUE (day, category_id)
);

-- 질문 구독 테이블
CREATE TABLE question_subscriptions
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT    NOT NULL,
    hour          INTEGER   NOT NULL,
    subscribed_at TIMESTAMP NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_subscriptions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_question_subscriptions_user UNIQUE (user_id)
);

-- 질문 구독 카테고리 테이블
CREATE TABLE question_subscription_categories
(
    id              BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT    NOT NULL,
    category_id     BIGINT    NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_qsc_subscription FOREIGN KEY (subscription_id) REFERENCES question_subscriptions (id) ON DELETE CASCADE,
    CONSTRAINT fk_qsc_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT uk_qsc_subscription_category UNIQUE (subscription_id, category_id)
);

-- 질문 배달 테이블
CREATE TABLE question_deliveries
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT    NOT NULL,
    category_id   BIGINT    NOT NULL,
    day           BIGINT    NOT NULL,
    question_id   BIGINT    NOT NULL,
    delivered_at  TIMESTAMP NOT NULL,
    success       BOOLEAN   NOT NULL DEFAULT TRUE,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_deliveries_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_question_deliveries_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_question_deliveries_question FOREIGN KEY (question_id) REFERENCES questions (id),
    CONSTRAINT uk_question_deliveries_user_category_day UNIQUE (user_id, category_id, day)
);

-- 인덱스
CREATE INDEX idx_question_subscriptions_hour ON question_subscriptions (hour);
CREATE INDEX idx_qsc_subscription ON question_subscription_categories (subscription_id);
CREATE INDEX idx_question_deliveries_user_category ON question_deliveries (user_id, category_id);
CREATE INDEX idx_question_deliveries_day ON question_deliveries (day);
CREATE INDEX idx_question_deliveries_delivered_at ON question_deliveries (delivered_at);
CREATE INDEX idx_question_deliveries_success ON question_deliveries (success);


