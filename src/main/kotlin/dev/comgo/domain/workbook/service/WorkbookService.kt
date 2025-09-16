package dev.comgo.domain.workbook.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.quiz.domain.repository.QuizRepository
import dev.comgo.domain.workbook.domain.entity.WorkbookEntity
import dev.comgo.domain.workbook.domain.entity.WorkbookQuizEntity
import dev.comgo.domain.workbook.domain.error.WorkbookErrorCode
import dev.comgo.domain.workbook.domain.repository.WorkbookQuizRepository
import dev.comgo.domain.workbook.domain.repository.WorkbookRepository
import dev.comgo.domain.workbook.presentation.dto.request.AddQuizRequest
import dev.comgo.domain.workbook.presentation.dto.response.WorkbookResponse
import dev.comgo.infra.security.holder.SecurityHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    suspend fun createWorkbook(request: CreateWorkbookRequest): WorkbookResponse {
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

    suspend fun deleteWorkbook(workbookId: Long) {
        val userId = securityHolder.getUserId()
        val workbook = workbookRepository.findByIdAndOwnerIdAndDeletedAtIsNull(workbookId, userId)
            ?: throw CustomException(WorkbookErrorCode.WORKBOOK_NOT_FOUND)

        transactionalOperator.executeAndAwait {
            workbookRepository.save(workbook.copy(deletedAt = Instant.now()))
            workbookQuizRepository.deleteAllByWorkbookId(workbookId)
        }
    }

    suspend fun addQuizToWorkbook(workbookId: Long, request: AddQuizRequest) {
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

    suspend fun removeQuizFromWorkbook(workbookId: Long, quizId: String) {
        val userId = securityHolder.getUserId()
        val workbook = workbookRepository.findByIdAndOwnerIdAndDeletedAtIsNull(workbookId, userId)
            ?: throw CustomException(WorkbookErrorCode.WORKBOOK_NOT_FOUND)
        workbookQuizRepository.deleteByWorkbookIdAndQuizId(workbook.id!!, quizId)
    }

    fun getMyWorkbooks(): Flow<WorkbookResponse> = flowWithQuizIds { ownerId ->
        workbookRepository.findAllByOwnerIdAndDeletedAtIsNull(ownerId)
    }

    fun getAllWorkbooks(): Flow<WorkbookResponse> = flow {
        workbookRepository.findAllByDeletedAtIsNull().collect { wb ->
            val quizIds = workbookQuizRepository.findAllByWorkbookId(wb.id!!).map { it.quizId }.toList()
            emit(toResponse(wb, quizIds))
        }
    }

    suspend fun getWorkbook(workbookId: Long): WorkbookResponse {
        val workbook = workbookRepository.findById(workbookId)
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

    private fun flowWithQuizIds(block: suspend (Long) -> Flow<WorkbookEntity>): Flow<WorkbookResponse> = flow {
        val userId = securityHolder.getUserId()
        block(userId).collect { wb ->
            val quizIds = workbookQuizRepository.findAllByWorkbookId(wb.id!!).map { it.quizId }.toList()
            emit(toResponse(wb, quizIds))
        }
    }

    private suspend fun getCurrentUserId(): Long? {
        return when {
            securityHolder.isAuthenticated() -> securityHolder.getUser().id
            else -> null
        }
    }
}
