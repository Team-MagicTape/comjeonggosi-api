package com.comjeonggosi.domain.quiz.presentation.dto.response

data class QuizSubmitResponse(
    val quiz: QuizResponse,
    val isCorrected: Boolean,
    val userAnswer: String
)
