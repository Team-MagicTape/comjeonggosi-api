package com.comjeonggosi.domain.question.domain.repository

import com.comjeonggosi.domain.question.domain.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuestionRepository: CoroutineCrudRepository<QuestionEntity, Long> {
    fun findAllByCategoryIdAndDayLessThanEqual(categoryId: Long, day: Long): Flow<QuestionEntity>
    suspend fun findByCategoryIdAndDay(categoryId: Long, day: Long): QuestionEntity?
}