package com.comjeonggosi.domain.quiz.presentation.dto.response

data class QuizSubmissionResponse(
    val quiz: QuizResponse,
    val isCorrected: Boolean,
    val userAnswer: String
)
