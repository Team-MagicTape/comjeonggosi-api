package com.comjeonggosi.domain.quiz.presentation.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class QuizSubmissionResponse(
    val quiz: QuizResponse,
    val isCorrected: Boolean,
    val userAnswer: String,
    @JsonIgnore val submittedAt: Instant = Instant.now()
)
