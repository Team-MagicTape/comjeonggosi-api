package dev.comgo.domain.notice.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.notice.domain.entity.NoticeEntity
import dev.comgo.domain.notice.domain.error.NoticeErrorCode
import dev.comgo.domain.notice.domain.repository.NoticeRepository
import dev.comgo.domain.notice.presentation.dto.response.NoticeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
) {
    fun getAll(): Flow<NoticeResponse> =
        noticeRepository.findAllByDeletedAtIsNull().map { it.toResponse() }

    suspend fun getById(id: Long): NoticeResponse {
        val entity = noticeRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw CustomException(NoticeErrorCode.NOTICE_NOT_FOUND)
        return entity.toResponse()
    }

    suspend fun create(request: dev.comgo.domain.admin.notice.presentation.dto.request.CreateNoticeRequest): NoticeResponse {
        val saved = noticeRepository.save(NoticeEntity(title = request.title, content = request.content))
        return saved.toResponse()
    }

    suspend fun delete(id: Long) {
        val entity = noticeRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw CustomException(NoticeErrorCode.NOTICE_NOT_FOUND)
        noticeRepository.save(entity.copy(deletedAt = Instant.now()))
    }

    private fun NoticeEntity.toResponse() = NoticeResponse(
        id = this.id!!,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt,
    )
}
