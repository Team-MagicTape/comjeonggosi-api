package dev.comgo.domain.question.domain.entity

import dev.comgo.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("questions")
data class QuestionEntity(
    @Id
    val id: Long? = null,

    val day: Long,
    val categoryId: Long,
    val title: String,
    val content: String,
    val answer: String
) : BaseEntity()