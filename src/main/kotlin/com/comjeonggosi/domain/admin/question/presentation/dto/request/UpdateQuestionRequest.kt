package com.comjeonggosi.domain.admin.question.presentation.dto.request

data class UpdateQuestionRequest(
    val title: String?,
    val content: String?,
    val answer: String?,
)