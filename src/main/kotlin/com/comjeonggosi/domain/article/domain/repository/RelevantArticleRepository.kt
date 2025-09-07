package com.comjeonggosi.domain.article.domain.repository

import com.comjeonggosi.domain.article.domain.entity.RelevantArticleEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RelevantArticleRepository : CoroutineCrudRepository<RelevantArticleEntity, Long> {
    suspend fun findByFromArticleIdAndToArticleId(fromArticleId: Long, toArticleId: Long): RelevantArticleEntity?
    fun findAllByFromArticleId(fromId: Long): Flow<RelevantArticleEntity>
}