package com.comjeonggosi.domain.question.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("question_subscriptions")
data class QuestionSubscriptionEntity(
    @Id
    val id: Long? = null,
    val userId: Long,
    val hour: Int,
    val subscribedAt: Instant
) : BaseEntity()