package com.comjeonggosi.domain.article.domain.repository

import com.comjeonggosi.domain.article.domain.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : CoroutineCrudRepository<ArticleEntity, Long> {
    fun findAllByDeletedAtIsNull(): Flow<ArticleEntity>
}