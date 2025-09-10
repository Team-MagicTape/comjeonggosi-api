-- 연관 아티클 테이블
CREATE TABLE relevant_articles
(
    id              BIGSERIAL PRIMARY KEY,
    from_article_id BIGINT  NOT NULL,
    to_article_id   BIGINT  NOT NULL,
    is_before       BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_relevant_from_article FOREIGN KEY (from_article_id) REFERENCES articles (id),
    CONSTRAINT fk_relevant_to_article FOREIGN KEY (to_article_id) REFERENCES articles (id),
    CONSTRAINT uk_relevant_articles UNIQUE (from_article_id, to_article_id, is_before)
);

CREATE INDEX idx_relevant_from ON relevant_articles (from_article_id, is_before);
CREATE INDEX idx_relevant_to ON relevant_articles (to_article_id, is_before);