package dev.comgo.domain.article.domain.entity

import dev.comgo.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "articles")
data class ArticleEntity(
    @Id
    val id: Long? = null,

    @Column("title")
    val title: String,

    @Column("content")
    val content: String,

    @Column("author_id")
    val authorId: Long,

    @Column("category_id")
    val categoryId: Long,

    @Column("deleted_at")
    val deletedAt: Instant? = null,
) : BaseEntity()