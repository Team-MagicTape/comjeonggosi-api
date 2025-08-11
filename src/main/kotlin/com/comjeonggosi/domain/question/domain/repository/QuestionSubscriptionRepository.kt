package com.comjeonggosi.domain.question.domain.repository

import com.comjeonggosi.domain.question.domain.entity.QuestionSubscriptionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuestionSubscriptionRepository: CoroutineCrudRepository<QuestionSubscriptionEntity, Long> {
    suspend fun findByUserId(userId: Long): QuestionSubscriptionEntity?
    suspend fun existsByUserId(userId: Long): Boolean

    fun findAllByHourAndMinute(hour: Int, minute: Int): Flow<QuestionSubscriptionEntity>
}