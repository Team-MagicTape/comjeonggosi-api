package com.comjeonggosi.domain.quiz.presentation.controller

import com.comjeonggosi.domain.quiz.application.service.QuizService
import com.comjeonggosi.domain.quiz.presentation.dto.request.SolveQuizRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quizzes")
class QuizController(
    private val quizService: QuizService
) {
    @GetMapping
    suspend fun getRandomQuiz(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) hidden: String?,
    ) = quizService.getRandomQuiz(categoryId, hidden)

    @PostMapping("/{quizId}/solve")
    suspend fun solveQuiz(@PathVariable quizId: String, @RequestBody request: SolveQuizRequest) =
        quizService.solve(quizId, request)

    @GetMapping("/submissions/my")
    suspend fun getMySubmissions(@RequestParam(required = false) isCorrected: Boolean?) =
        quizService.getMySubmissions(isCorrected)
}