package com.comjeonggosi.domain.workbook.presentation.controller

import com.comjeonggosi.domain.workbook.presentation.dto.request.AddQuizRequest
import com.comjeonggosi.domain.workbook.service.WorkbookService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/workbooks")
class WorkbookController(
    private val workbookService: WorkbookService
) {
    @PostMapping
    suspend fun create(@RequestBody request: WorkbookService.CreateWorkbookRequest) =
        workbookService.createWorkbook(request)

    @GetMapping
    fun getAllWorkbooks() = workbookService.getAllWorkbooks()

    @DeleteMapping("/{workbookId}")
    suspend fun delete(@PathVariable workbookId: Long) =
        workbookService.deleteWorkbook(workbookId)

    @PostMapping("/{workbookId}/quizzes")
    suspend fun addQuiz(
        @PathVariable workbookId: Long,
        @RequestBody request: AddQuizRequest
    ) = workbookService.addQuizToWorkbook(workbookId, request)

    @DeleteMapping("/{workbookId}/quizzes/{quizId}")
    suspend fun removeQuiz(
        @PathVariable workbookId: Long,
        @PathVariable quizId: String
    ) = workbookService.removeQuizFromWorkbook(workbookId, quizId)

    @GetMapping("/my")
    fun getMyWorkbooks() = workbookService.getMyWorkbooks()

    @GetMapping("/{workbookId}")
    suspend fun getWorkbook(@PathVariable workbookId: Long) =
        workbookService.getWorkbook(workbookId)
}
