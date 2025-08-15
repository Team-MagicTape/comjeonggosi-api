package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuizRepository : CoroutineCrudRepository<QuizDocument, String> {
    fun findAllByDeletedAtIsNull(): Flow<QuizDocument>
    fun findAllByCategoryIdAndDeletedAtIsNull(categoryId: Long): Flow<QuizDocument>
}