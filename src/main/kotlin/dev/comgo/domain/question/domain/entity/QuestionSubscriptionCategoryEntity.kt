package dev.comgo.domain.question.domain.entity

import dev.comgo.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("question_subscription_categories")
data class QuestionSubscriptionCategoryEntity(
    @Id val id: Long? = null,
    val subscriptionId: Long,
    val categoryId: Long
) : BaseEntity()