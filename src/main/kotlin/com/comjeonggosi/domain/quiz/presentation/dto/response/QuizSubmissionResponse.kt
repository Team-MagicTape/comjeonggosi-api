package com.comjeonggosi.domain.quiz.presentation.dto.response

import java.time.Instant

data class QuizSubmissionResponse(
    val quiz: QuizResponse,
    val isCorrected: Boolean,
    val userAnswer: String,
    val submittedAt: Instant
)
