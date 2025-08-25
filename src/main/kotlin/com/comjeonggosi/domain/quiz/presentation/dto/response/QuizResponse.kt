package com.comjeonggosi.domain.quiz.presentation.dto.response

import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import com.comjeonggosi.domain.quiz.domain.enums.QuizType

data class QuizResponse(
    val id: String,
    val content: String,
    val answer: String,
    val options: List<String>,
    val category: CategoryResponse,
    val articleId: Long?,
    val type: QuizType,
)
