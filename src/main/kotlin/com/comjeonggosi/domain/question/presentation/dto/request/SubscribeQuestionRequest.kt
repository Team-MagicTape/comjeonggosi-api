package com.comjeonggosi.domain.question.presentation.dto.request

import org.hibernate.validator.constraints.Range

data class SubscribeQuestionRequest(
    @field:Range(min = 0, max = 23)
    val hour: Int,
    val categoryIds: List<Long>,
    val email: String? = null,
)