package com.comjeonggosi.domain.article.domain.repository

import com.comjeonggosi.domain.article.domain.entity.ArticleEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : CoroutineCrudRepository<ArticleEntity, Long> {
}