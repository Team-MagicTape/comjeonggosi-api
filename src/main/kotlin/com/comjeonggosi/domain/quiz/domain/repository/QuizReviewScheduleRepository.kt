package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.QuizReviewScheduleEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant

interface QuizReviewScheduleRepository : CoroutineCrudRepository<QuizReviewScheduleEntity, Long> {
    fun findByUserId(userId: Long): Flow<QuizReviewScheduleEntity>
    suspend fun findByUserIdAndQuizId(userId: Long, quizId: String): QuizReviewScheduleEntity?
    fun findByUserIdAndNextReviewAtBefore(userId: Long, time: Instant): Flow<QuizReviewScheduleEntity>
    fun findByUserIdAndIsMasteredFalse(userId: Long): Flow<QuizReviewScheduleEntity>
}