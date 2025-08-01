package com.comjeonggosi.domain.article.application.service

import com.comjeonggosi.domain.article.domain.entity.ArticleEntity
import com.comjeonggosi.domain.article.domain.repository.ArticleRepository
import com.comjeonggosi.domain.article.presentation.dto.response.ArticleResponse
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend fun getArticles(): Flow<ArticleResponse> {
        return articleRepository.findAllByDeletedAtIsNull().map { it.toResponse() }
    }

    suspend fun getArticle(articleId: Long): ArticleResponse {
        return articleRepository.findById(articleId)?.toResponse()
            ?: throw IllegalArgumentException("article not found")
    }

    private suspend fun ArticleEntity.toResponse(): ArticleResponse {
        val category = categoryRepository.findById(this.categoryId)
            ?: throw IllegalArgumentException("category not found")

        return ArticleResponse(
            id = this.id!!,
            title = this.title,
            content = this.content,
            category = CategoryResponse(
                id = category.id!!,
                name = category.name,
                description = category.description
            )
        )
    }
}