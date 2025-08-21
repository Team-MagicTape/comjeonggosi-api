package com.comjeonggosi.domain.admin.quiz.presentation.request.dto

data class UpdateQuizRequest(
    val content: String?,
    val answer: String?,
    val options: List<String>?,
    val categoryId: Long?,
    val articleId: Long?
)
