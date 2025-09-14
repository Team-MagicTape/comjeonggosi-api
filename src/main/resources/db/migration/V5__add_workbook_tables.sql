-- 문제집
CREATE TABLE workbooks
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    owner_id    BIGINT       NOT NULL,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workbooks_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE INDEX idx_workbooks_owner ON workbooks (owner_id);
CREATE INDEX idx_workbooks_deleted_at ON workbooks (deleted_at);

CREATE TABLE workbooks_quizzes
(
    id          BIGSERIAL PRIMARY KEY,
    workbook_id BIGINT       NOT NULL,
    quiz_id     VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workbooks_quizzes_workbook FOREIGN KEY (workbook_id) REFERENCES workbooks (id) ON DELETE CASCADE,
    CONSTRAINT uk_workbooks_quizzes UNIQUE (workbook_id, quiz_id)
);

CREATE INDEX idx_workbooks_quizzes_workbook ON workbooks_quizzes (workbook_id);
CREATE INDEX idx_workbooks_quizzes_quiz ON workbooks_quizzes (quiz_id);

-- 공지
CREATE TABLE IF NOT EXISTS notices
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    title
    VARCHAR
(
    255
) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
                         WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
                         WITH TIME ZONE
                             );

CREATE INDEX IF NOT EXISTS idx_notices_deleted_at ON notices (deleted_at);