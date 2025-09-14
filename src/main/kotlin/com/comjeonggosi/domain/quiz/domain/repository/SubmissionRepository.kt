package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubmissionRepository : CoroutineCrudRepository<SubmissionEntity, Long> {
    
    @Query("""
        SELECT * FROM submissions 
        WHERE user_id = :userId 
        AND (:isCorrected IS NULL OR is_corrected = :isCorrected)
        ORDER BY created_at DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun findByUserId(
        userId: Long,
        limit: Int,
        offset: Long,
        isCorrected: Boolean?
    ): Flow<SubmissionEntity>

    @Query(
        """
        SELECT quiz_id
        FROM submissions
        WHERE user_id = :userId
        GROUP BY quiz_id
        ORDER BY MAX(created_at) DESC
        LIMIT :limit
        """
    )
    suspend fun findRecentSolvedIds(userId: Long, limit: Int): List<String>
}