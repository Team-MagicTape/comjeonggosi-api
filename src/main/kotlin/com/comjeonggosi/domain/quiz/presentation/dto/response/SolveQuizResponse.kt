package com.comjeonggosi.domain.quiz.presentation.dto.response

data class SolveQuizResponse(
    val isCorrect: Boolean,
    val answer: String
)