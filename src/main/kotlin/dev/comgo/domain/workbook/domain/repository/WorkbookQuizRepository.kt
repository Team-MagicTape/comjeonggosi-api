package dev.comgo.domain.workbook.domain.repository

import dev.comgo.domain.workbook.domain.entity.WorkbookQuizEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface WorkbookQuizRepository : CoroutineCrudRepository<WorkbookQuizEntity, Long> {
    fun findAllByWorkbookId(workbookId: Long): Flow<WorkbookQuizEntity>
    suspend fun existsByWorkbookIdAndQuizId(workbookId: Long, quizId: String): Boolean
    suspend fun deleteByWorkbookIdAndQuizId(workbookId: Long, quizId: String)
    suspend fun deleteAllByWorkbookId(workbookId: Long)
}