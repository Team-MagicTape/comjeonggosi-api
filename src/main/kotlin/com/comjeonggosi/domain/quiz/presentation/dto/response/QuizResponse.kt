package com.comjeonggosi.domain.quiz.presentation.dto.response

import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse

data class QuizResponse(
    val id: String,
    val content: String,
    val answer: String,
    val options: List<String>,
    val category: CategoryResponse,
    val articleId: Long?
)
