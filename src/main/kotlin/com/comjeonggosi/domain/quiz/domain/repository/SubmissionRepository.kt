package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubmissionRepository : CoroutineCrudRepository<SubmissionEntity, Long> {
    @Query("SELECT * FROM submissions WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun findAllByUserId(userId: Long, limit: Int, offset: Long): Flow<SubmissionEntity>
    
    @Query("SELECT * FROM submissions WHERE user_id = :userId AND is_corrected = :isCorrected ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun findAllByUserIdAndIsCorrected(
        userId: Long,
        isCorrected: Boolean,
        limit: Int,
        offset: Long
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
    suspend fun findSolvedProblemIdsWithinDays(userId: Long, days: Long): List<String>

    @Query(
        """
        SELECT DISTINCT quiz_id 
        FROM submissions
        WHERE user_id = :userId 
          AND is_corrected = true
        """
    )
    suspend fun findAllSolvedProblemIds(userId: Long): List<String>
}