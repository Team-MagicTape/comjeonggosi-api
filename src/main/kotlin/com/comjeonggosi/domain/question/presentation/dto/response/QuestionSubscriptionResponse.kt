package com.comjeonggosi.domain.question.presentation.dto.response

data class QuestionSubscriptionResponse(
    val hour: Int,
    val categoryIds: List<Long>
)