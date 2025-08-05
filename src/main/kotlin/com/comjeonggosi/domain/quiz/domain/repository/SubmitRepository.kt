package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.SubmitEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubmitRepository : CoroutineCrudRepository<SubmitEntity, Long> {
    fun findAllByUserId(userId: Long): Flow<SubmitEntity>
}