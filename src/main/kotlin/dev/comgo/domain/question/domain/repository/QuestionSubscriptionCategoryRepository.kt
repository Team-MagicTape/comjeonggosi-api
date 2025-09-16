package dev.comgo.domain.question.domain.repository

import dev.comgo.domain.question.domain.entity.QuestionSubscriptionCategoryEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuestionSubscriptionCategoryRepository : CoroutineCrudRepository<QuestionSubscriptionCategoryEntity, Long> {
    fun findAllBySubscriptionId(subscriptionId: Long): Flow<QuestionSubscriptionCategoryEntity>
    fun findAllBySubscriptionIdIn(subscriptionIds: List<Long>): Flow<QuestionSubscriptionCategoryEntity>

    suspend fun existsBySubscriptionIdAndCategoryId(subscriptionId: Long, categoryId: Long): Boolean
}