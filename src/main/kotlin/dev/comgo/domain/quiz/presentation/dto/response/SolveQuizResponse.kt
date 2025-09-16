package dev.comgo.domain.quiz.presentation.dto.response

data class SolveQuizResponse(
    val isCorrect: Boolean,
    val answer: String
)