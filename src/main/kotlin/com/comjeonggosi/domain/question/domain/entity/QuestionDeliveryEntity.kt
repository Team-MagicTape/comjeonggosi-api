package com.comjeonggosi.domain.question.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("question_deliveries")
data class QuestionDeliveryEntity(
    @Id val id: Long? = null,
    val userId: Long,
    val categoryId: Long,
    val day: Long,
    val questionId: Long,
    val deliveredAt: Instant,
    val success: Boolean = true,
    val errorMessage: String? = null
)