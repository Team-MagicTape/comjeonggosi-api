package com.comjeonggosi.domain.post.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import com.comjeonggosi.domain.post.domain.enums.PostCategory
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "posts")
data class PostEntity(
    @Id
    val id: Long? = null,

    @Column("title")
    var title: String,

    @Column("content")
    var content: String,

    @Column("author_id")
    var authorId: Long,

    @Column("category")
    var category: PostCategory,

    @Column("is_deleted")
    var isDeleted: Boolean = false,
) : BaseEntity()