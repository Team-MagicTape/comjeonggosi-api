package dev.comgo.domain.quiz.presentation.dto.response

import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
import dev.comgo.domain.quiz.domain.enums.QuizType

data class QuizResponse(
    val id: String,
    val content: String,
    val answer: String,
    val options: List<String>,
    val category: CategoryResponse,
    val articleId: Long?,
    val type: QuizType,
    val difficulty: Int = 3,
    val explanation: String?,
    val imageUrl: String?,
)
