package com.comjeonggosi.domain.notice.domain.repository

import com.comjeonggosi.domain.notice.domain.entity.NoticeEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : CoroutineCrudRepository<NoticeEntity, Long> {
    fun findAllByDeletedAtIsNull(): Flow<NoticeEntity>
    suspend fun findByIdAndDeletedAtIsNull(id: Long): NoticeEntity?
}
