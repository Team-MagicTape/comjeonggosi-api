package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant

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
        SELECT DISTINCT quiz_id 
        FROM submissions
        WHERE user_id = :userId 
          AND is_corrected = true 
          AND created_at >= NOW() - INTERVAL :days DAY
        """
    )
    suspend fun findRecentSolvedIds(userId: Long, limit: Int): List<String>
}