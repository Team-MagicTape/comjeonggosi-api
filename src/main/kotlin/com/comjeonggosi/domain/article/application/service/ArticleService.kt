package com.comjeonggosi.domain.article.application.service

import com.comjeonggosi.domain.article.domain.entity.ArticleEntity
import com.comjeonggosi.domain.article.domain.repository.ArticleRepository
import com.comjeonggosi.domain.article.presentation.dto.response.ArticleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {
    suspend fun getArticles(): Flow<ArticleResponse> {
        return articleRepository.findAll().map { it.toResponse() }
    }

    suspend fun getArticle(articleId: Long): ArticleResponse {
        return articleRepository.findById(articleId)?.toResponse()
            ?: throw IllegalArgumentException("article not found")
    }

    private fun ArticleEntity.toResponse(): ArticleResponse =
        ArticleResponse(
            id = this.id!!,
            title = this.title,
            content = this.content,
        )
}