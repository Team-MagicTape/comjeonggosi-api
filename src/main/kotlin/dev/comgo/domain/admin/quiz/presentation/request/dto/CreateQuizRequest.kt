package dev.comgo.domain.admin.quiz.presentation.request.dto

import dev.comgo.domain.quiz.domain.enums.QuizType

data class CreateQuizRequest(
    val content: String,
    val answer: String,
    val options: List<String>?,
    val categoryId: Long,
    val articleId: Long?,
    val type: QuizType,
    val difficulty: Int = 3,
    val explanation: String?,
    val imageUrl: String?,
)
