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
    var title: String,

    @Column("content")
    var content: String,

    @Column("author_id")
    var authorId: Long,

    @Column("category_id")
    var categoryId: Long,

    @Column("is_deleted")
    var isDeleted: Boolean = false,
) : BaseEntity()