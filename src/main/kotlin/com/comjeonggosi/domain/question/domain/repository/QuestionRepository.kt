package com.comjeonggosi.domain.question.domain.repository

import com.comjeonggosi.domain.question.domain.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuestionRepository: CoroutineCrudRepository<QuestionEntity, Long> {
    suspend fun findByDayAndCategoryId(day: Long, categoryId: Long): QuestionEntity?

    suspend fun existsByDayAndCategoryId(day: Long, categoryId: Long): Boolean

    fun findAllByOrderByDayAsc(): Flow<QuestionEntity>

    fun findAllByDayLessThanEqualOrderByDayAsc(day: Long): Flow<QuestionEntity>
    
    fun findAllByDayLessThanEqualAndCategoryIdInOrderByDayAsc(day: Long, categoryIds: List<Long>): Flow<QuestionEntity>
    
    fun findAllByDayAndCategoryIdInOrderByDayAsc(day: Long, categoryIds: List<Long>): Flow<QuestionEntity>
}