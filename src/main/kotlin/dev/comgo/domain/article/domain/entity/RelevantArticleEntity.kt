package dev.comgo.domain.article.domain.entity

import dev.comgo.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "relevant_articles")
data class RelevantArticleEntity(
    @Id
    val id: Long? = null,

    @Column("from_article_id")
    val fromArticleId: Long,

    @Column("to_article_id")
    val toArticleId: Long,

    @Column("is_before")
    val isBefore: Boolean,
) : BaseEntity()