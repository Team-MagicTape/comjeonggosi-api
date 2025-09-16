package dev.comgo.domain.workbook.presentation.dto.response

data class WorkbookResponse(
    val id: Long,
    val name: String,
    val description: String,
    val quizIds: List<String>,
)
