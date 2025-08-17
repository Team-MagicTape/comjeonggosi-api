package com.comjeonggosi.domain.question.presentation.dto.request

data class SubscribeQuestionRequest(
    val hour: Int,
    val categoryIds: List<Long>
)