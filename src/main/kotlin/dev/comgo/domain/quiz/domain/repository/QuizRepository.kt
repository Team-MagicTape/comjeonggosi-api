package dev.comgo.domain.quiz.domain.repository

import dev.comgo.domain.quiz.domain.document.QuizDocument
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuizRepository : CoroutineCrudRepository<QuizDocument, String> {
    fun findAllByDeletedAtIsNull(): Flow<QuizDocument>
    fun findAllByCategoryIdAndDeletedAtIsNull(categoryId: Long): Flow<QuizDocument>
}