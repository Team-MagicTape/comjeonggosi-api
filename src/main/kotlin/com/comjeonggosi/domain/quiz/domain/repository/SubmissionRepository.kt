package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubmissionRepository : CoroutineCrudRepository<SubmissionEntity, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): Flow<SubmissionEntity>
    fun findAllByUserIdAndIsCorrectedOrderByCreatedAtDesc(
        userId: Long,
        isCorrected: Boolean,
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