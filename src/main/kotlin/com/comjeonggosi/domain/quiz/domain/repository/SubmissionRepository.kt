package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubmissionRepository : CoroutineCrudRepository<SubmissionEntity, Long> {
    fun findAllByUserId(userId: Long): Flow<SubmissionEntity>
    fun findAllByUserIdAndIsCorrected(userId: Long, isCorrected: Boolean): Flow<SubmissionEntity>
}