package com.comjeonggosi.domain.quiz.presentation.controller

import com.comjeonggosi.domain.quiz.application.service.QuizService
import com.comjeonggosi.domain.quiz.domain.enums.QuizMode
import com.comjeonggosi.domain.quiz.presentation.dto.request.SolveQuizRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quizzes")
class QuizController(
    private val quizService: QuizService
) {
    @GetMapping
    suspend fun getQuiz(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false, defaultValue = "RANDOM") mode: String = "RANDOM",
        @RequestParam(required = false) difficulty: Int?,
        @RequestParam(required = false) tags: List<String>?
    ) = quizService.getQuiz(categoryId, QuizMode.valueOf(mode), difficulty, tags)

    @PostMapping("/{quizId}/solve")
    suspend fun solveQuiz(@PathVariable quizId: String, @RequestBody request: SolveQuizRequest) =
        quizService.solve(quizId, request)

    @GetMapping("/submissions/my")
    suspend fun getMySubmissions(
        @RequestParam(required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) size: Int,
        @RequestParam(required = false) isCorrected: Boolean?
    ) = quizService.getMySubmissions(page, size, isCorrected)
}