package com.comjeonggosi.domain.admin.quiz.presentation.request.dto

import com.comjeonggosi.domain.quiz.domain.enums.QuizType

data class UpdateQuizRequest(
    val content: String?,
    val answer: String?,
    val options: List<String>?,
    val categoryId: Long?,
    val articleId: Long?,
    val type: QuizType?,
)
