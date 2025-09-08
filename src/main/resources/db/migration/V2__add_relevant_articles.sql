CREATE TABLE relevant_articles
(
    id              BIGSERIAL PRIMARY KEY,
    from_article_id BIGINT  NOT NULL,
    to_article_id   BIGINT  NOT NULL,
    is_before       BOOLEAN NOT NULL,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_article_id) REFERENCES articles (id),
    FOREIGN KEY (to_article_id) REFERENCES articles (id)
);

CREATE INDEX idx_relevant_from ON relevant_articles (from_article_id, is_before);
CREATE INDEX idx_relevant_to ON relevant_articles (to_article_id, is_before);