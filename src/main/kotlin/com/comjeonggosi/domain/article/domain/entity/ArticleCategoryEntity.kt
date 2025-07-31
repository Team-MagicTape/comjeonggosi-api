package com.comjeonggosi.domain.article.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_categories")
data class ArticleCategoryEntity(
    @Id
    val id: Long? = null,

    @Column("name")
    val name: String,

    @Column("description")
    val description: String,
)