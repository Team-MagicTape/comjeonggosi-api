package dev.comgo.domain.workbook.domain.entity

import dev.comgo.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("workbooks_quizzes")
data class WorkbookQuizEntity(
    @Id
    val id: Long? = null,
    val workbookId: Long,
    val quizId: String,
) : BaseEntity()
