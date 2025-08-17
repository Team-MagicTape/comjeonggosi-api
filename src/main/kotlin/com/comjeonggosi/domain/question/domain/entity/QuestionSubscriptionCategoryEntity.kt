package com.comjeonggosi.domain.question.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("question_subscription_categories")
data class QuestionSubscriptionCategoryEntity(
    @Id val id: Long? = null,
    val subscriptionId: Long,
    val categoryId: Long
) : BaseEntity()