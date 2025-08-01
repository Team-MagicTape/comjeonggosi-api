package com.comjeonggosi.domain.article.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

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

    @Column("is_deleted")
    val isDeleted: Boolean = false,
) : BaseEntity()