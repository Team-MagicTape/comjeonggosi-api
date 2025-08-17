package com.comjeonggosi.domain.question.presentation.dto.response

data class QuestionResponse(
    val id: Long,
    val day: Long,
    val categoryId: Long,
    val title: String,
    val content: String,
    val answer: String
)