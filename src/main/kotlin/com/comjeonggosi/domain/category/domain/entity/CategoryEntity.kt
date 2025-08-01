package com.comjeonggosi.domain.category.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "categories")
data class CategoryEntity(
    @Id
    val id: Long? = null,

    @Column("name")
    val name: String,

    @Column("description")
    val description: String,

    @Column("deleted_at")
    val deletedAt: Instant? = null,
) : BaseEntity()