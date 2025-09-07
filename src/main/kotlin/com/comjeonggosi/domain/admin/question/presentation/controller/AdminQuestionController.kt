package com.comjeonggosi.domain.admin.question.presentation.controller

import com.comjeonggosi.domain.admin.question.application.service.AdminQuestionService
import com.comjeonggosi.domain.admin.question.presentation.dto.request.CreateQuestionRequest
import com.comjeonggosi.domain.admin.question.presentation.dto.request.UpdateQuestionRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/questions")
class AdminQuestionController(private val adminQuestionService: AdminQuestionService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createQuestion(@RequestBody request: CreateQuestionRequest) =
        adminQuestionService.createQuestion(request)

    @PatchMapping("/{questionId}")
    suspend fun updateQuestion(@PathVariable questionId: Long, @RequestBody request: UpdateQuestionRequest) =
        adminQuestionService.updateQuestion(questionId, request)
}