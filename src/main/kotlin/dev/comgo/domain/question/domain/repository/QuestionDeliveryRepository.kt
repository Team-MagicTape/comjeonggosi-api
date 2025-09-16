package dev.comgo.domain.question.domain.repository

import dev.comgo.domain.question.domain.entity.QuestionDeliveryEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionDeliveryRepository : CoroutineCrudRepository<QuestionDeliveryEntity, Long> {
    suspend fun findTopByUserIdAndCategoryIdOrderByDayDesc(userId: Long, categoryId: Long): QuestionDeliveryEntity?
    suspend fun existsByUserIdAndCategoryIdAndDay(userId: Long, categoryId: Long, day: Long): Boolean
}