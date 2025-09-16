package dev.comgo.domain.notice.domain.repository

import dev.comgo.domain.notice.domain.entity.NoticeEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : CoroutineCrudRepository<NoticeEntity, Long> {
    fun findAllByDeletedAtIsNull(): Flow<NoticeEntity>
    suspend fun findByIdAndDeletedAtIsNull(id: Long): NoticeEntity?
}
