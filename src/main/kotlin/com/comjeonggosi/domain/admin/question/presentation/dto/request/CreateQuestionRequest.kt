package com.comjeonggosi.domain.admin.question.presentation.dto.request

data class CreateQuestionRequest(
    val title: String,
    val content: String,
    val answer: String,
    val day: Long,
    val categoryId: Long,
)