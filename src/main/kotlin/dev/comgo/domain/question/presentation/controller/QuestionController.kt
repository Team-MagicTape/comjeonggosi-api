package dev.comgo.domain.question.presentation.controller

import dev.comgo.domain.question.application.service.QuestionService
import dev.comgo.domain.question.presentation.dto.request.SubscribeQuestionRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(
    private val questionService: QuestionService
) {
    @GetMapping
    suspend fun getQuestions(@RequestParam categoryId: Long) = questionService.getQuestions(categoryId)

    @GetMapping("/{questionId}")
    suspend fun getQuestion(@PathVariable questionId: Long) = questionService.getQuestion(questionId)

    @GetMapping("/subscription")
    suspend fun getSubscription() = questionService.getSubscription()

    @PostMapping("/subscribe")
    suspend fun subscribe(@RequestBody request: SubscribeQuestionRequest) = questionService.subscribe(request)
}