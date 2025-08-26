package com.comjeonggosi.domain.workbook.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.workbook.domain.error.WorkbookErrorCode
import com.comjeonggosi.domain.quiz.domain.repository.QuizRepository
import com.comjeonggosi.domain.workbook.domain.entity.WorkbookEntity
import com.comjeonggosi.domain.workbook.domain.entity.WorkbookQuizEntity
import com.comjeonggosi.domain.workbook.domain.repository.WorkbookQuizRepository
import com.comjeonggosi.domain.workbook.domain.repository.WorkbookRepository
import com.comjeonggosi.infra.security.holder.SecurityHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.Instant

@Service
class WorkbookService(
    private val securityHolder: SecurityHolder,
    private val workbookRepository: WorkbookRepository,
    private val workbookQuizRepository: WorkbookQuizRepository,
    private val quizRepository: QuizRepository,
    private val transactionalOperator: TransactionalOperator,
) {
    data class CreateWorkbookRequest(val name: String, val description: String)
    data class AddQuizRequest(val quizId: String)
    data class WorkbookResponse(
        val id: Long,
        val name: String,
        val description: String,
        val quizIds: List<String>,
    )

    suspend fun create(request: CreateWorkbookRequest): WorkbookResponse {
        val userId = securityHolder.getUserId()
        val saved = workbookRepository.save(
            WorkbookEntity(
                name = request.name,
                description = request.description,
                ownerId = userId,
                deletedAt = null,
            )
        )
        return toResponse(saved, emptyList())
    }

    suspend fun delete(workbookId: Long) {
        val userId = securityHolder.getUserId()
        val workbook = workbookRepository.findByIdAndOwnerIdAndDeletedAtIsNull(workbookId, userId)
            ?: throw CustomException(WorkbookErrorCode.WORKBOOK_NOT_FOUND)

        transactionalOperator.executeAndAwait {
            workbookRepository.save(workbook.copy(deletedAt = Instant.now()))
            workbookQuizRepository.deleteAllByWorkbookId(workbookId)
        }
    }

    suspend fun addQuiz(workbookId: Long, request: AddQuizRequest) {
        val userId = securityHolder.getUserId()
        val workbook = workbookRepository.findByIdAndOwnerIdAndDeletedAtIsNull(workbookId, userId)
            ?: throw CustomException(WorkbookErrorCode.WORKBOOK_NOT_FOUND)
        val quiz = quizRepository.findById(request.quizId) ?: throw CustomException(WorkbookErrorCode.QUIZ_NOT_FOUND)
        if (!workbookQuizRepository.existsByWorkbookIdAndQuizId(workbook.id!!, quiz.id!!)) {
            workbookQuizRepository.save(
                WorkbookQuizEntity(
                    workbookId = workbook.id,
                    quizId = quiz.id,
                )
            )
        }
    }

    suspend fun removeQuiz(workbookId: Long, quizId: String) {
        val userId = securityHolder.getUserId()
        val workbook = workbookRepository.findByIdAndOwnerIdAndDeletedAtIsNull(workbookId, userId)
            ?: throw CustomException(WorkbookErrorCode.WORKBOOK_NOT_FOUND)
        workbookQuizRepository.deleteByWorkbookIdAndQuizId(workbook.id!!, quizId)
    }

    fun getMyWorkbooks(): Flow<WorkbookResponse> = flowWithQuizIds { ownerId ->
        workbookRepository.findAllByOwnerIdAndDeletedAtIsNull(ownerId)
    }

    suspend fun getWorkbook(workbookId: Long): WorkbookResponse {
        val userId = securityHolder.getUserId()
        val workbook = workbookRepository.findByIdAndOwnerIdAndDeletedAtIsNull(workbookId, userId)
            ?: throw CustomException(WorkbookErrorCode.WORKBOOK_NOT_FOUND)
        val quizIds = workbookQuizRepository.findAllByWorkbookId(workbook.id!!).map { it.quizId }.toList()
        return toResponse(workbook, quizIds)
    }

    private fun toResponse(entity: WorkbookEntity, quizIds: List<String>): WorkbookResponse =
        WorkbookResponse(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            quizIds = quizIds,
        )

    private fun flowWithQuizIds(block: suspend (Long) -> Flow<WorkbookEntity>): Flow<WorkbookResponse> = kotlinx.coroutines.flow.flow {
        val userId = securityHolder.getUserId()
        block(userId).collect { wb ->
            val quizIds = workbookQuizRepository.findAllByWorkbookId(wb.id!!).map { it.quizId }.toList()
            emit(toResponse(wb, quizIds))
        }
    }
}
