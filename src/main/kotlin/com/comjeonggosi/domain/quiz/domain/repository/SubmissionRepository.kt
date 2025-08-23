package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubmissionRepository : CoroutineCrudRepository<SubmissionEntity, Long> {
    @Query("SELECT * FROM submissions WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun findAllByUserId(userId: Long, limit: Int, offset: Int): Flow<SubmissionEntity>
    
    @Query("SELECT * FROM submissions WHERE user_id = :userId AND is_corrected = :isCorrected ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun findAllByUserIdAndIsCorrected(
        userId: Long,
        isCorrected: Boolean,
        limit: Int,
        offset: Int
    ): Flow<SubmissionEntity>
}