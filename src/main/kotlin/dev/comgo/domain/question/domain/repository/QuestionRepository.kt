package dev.comgo.domain.question.domain.repository

import dev.comgo.domain.question.domain.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuestionRepository : CoroutineCrudRepository<QuestionEntity, Long> {
    fun findAllByCategoryIdAndDayLessThanEqual(categoryId: Long, day: Long): Flow<QuestionEntity>
    suspend fun findByCategoryIdAndDay(categoryId: Long, day: Long): QuestionEntity?

    suspend fun existsByCategoryIdAndDay(categoryId: Long, day: Long): Boolean
}