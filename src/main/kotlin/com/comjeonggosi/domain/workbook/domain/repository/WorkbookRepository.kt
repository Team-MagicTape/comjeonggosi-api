package com.comjeonggosi.domain.workbook.domain.repository

import com.comjeonggosi.domain.workbook.domain.entity.WorkbookEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkbookRepository: CoroutineCrudRepository<WorkbookEntity, Long> {
    fun findAllByOwnerIdAndDeletedAtIsNull(ownerId: Long): Flow<WorkbookEntity>
    suspend fun findByIdAndOwnerIdAndDeletedAtIsNull(id: Long, ownerId: Long): WorkbookEntity?
}