package com.comjeonggosi.domain.quiz.presentation.dto.response

data class QuizResponse(
    val id: String,
    val content: String,
    val answer: String,
    val options: List<String>,
    val categoryId: Long
)
