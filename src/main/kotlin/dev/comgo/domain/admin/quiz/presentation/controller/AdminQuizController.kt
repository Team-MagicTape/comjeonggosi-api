package dev.comgo.domain.admin.quiz.presentation.controller

import dev.comgo.domain.admin.quiz.application.service.AdminQuizService
import dev.comgo.domain.admin.quiz.presentation.request.dto.CreateQuizRequest
import dev.comgo.domain.admin.quiz.presentation.request.dto.UpdateQuizRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/quizzes")
class AdminQuizController(
    private val adminQuizService: AdminQuizService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createQuiz(@RequestBody request: CreateQuizRequest) = adminQuizService.createQuiz(request)

    @GetMapping
    suspend fun getQuizzes(@RequestParam(required = false) categoryId: Long?) =
        adminQuizService.getQuizzes(categoryId)

    @PatchMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updateQuiz(@PathVariable quizId: String, @RequestBody request: UpdateQuizRequest) =
        adminQuizService.updateQuiz(quizId, request)

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteQuiz(@PathVariable quizId: String) = adminQuizService.deleteQuiz(quizId)
}