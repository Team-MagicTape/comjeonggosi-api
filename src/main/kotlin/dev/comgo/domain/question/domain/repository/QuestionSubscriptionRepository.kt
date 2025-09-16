package dev.comgo.domain.question.domain.repository

import dev.comgo.domain.question.domain.entity.QuestionSubscriptionEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuestionSubscriptionRepository : CoroutineCrudRepository<QuestionSubscriptionEntity, Long> {
    suspend fun findByUserId(userId: Long): QuestionSubscriptionEntity?
    suspend fun findAllByHour(hour: Int): List<QuestionSubscriptionEntity>
}