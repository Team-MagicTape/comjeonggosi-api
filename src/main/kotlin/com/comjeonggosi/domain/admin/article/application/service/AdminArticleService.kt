package com.comjeonggosi.domain.admin.article.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.admin.article.presentation.dto.request.CreateArticleRequest
import com.comjeonggosi.domain.admin.article.presentation.dto.request.UpdateArticleRequest
import com.comjeonggosi.domain.article.domain.entity.ArticleEntity
import com.comjeonggosi.domain.article.domain.error.ArticleErrorCode
import com.comjeonggosi.domain.article.domain.repository.ArticleRepository
import com.comjeonggosi.domain.article.presentation.dto.response.ArticleResponse
import com.comjeonggosi.domain.category.domain.error.CategoryErrorCode
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import com.comjeonggosi.infra.security.holder.SecurityHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AdminArticleService(
    private val articleRepository: ArticleRepository,
    private val securityHolder: SecurityHolder,
    private val categoryRepository: CategoryRepository
) {
    suspend fun createArticle(request: CreateArticleRequest) {
        val me = securityHolder.getUser()

        articleRepository.save(
            ArticleEntity(
                title = request.title,
                content = request.content,
                authorId = me.id!!,
                categoryId = request.categoryId,
            )
        )
    }

    suspend fun updateArticle(articleId: Long, request: UpdateArticleRequest) {
        val article = articleRepository.findById(articleId)
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)

        articleRepository.save(
            article.copy(
                title = request.title ?: article.title,
                content = request.content ?: article.content
            )
        )
    }

    suspend fun deleteArticle(articleId: Long) {
        val article = articleRepository.findById(articleId)
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)

        articleRepository.save(
            article.copy(
                deletedAt = Instant.now(),
            )
        )
    }

    suspend fun getArticles(): Flow<ArticleResponse> {
        return articleRepository.findAllByDeletedAtIsNull().map { it.toResponse() }
    }

    suspend fun getArticle(articleId: Long): ArticleResponse {
        return articleRepository.findById(articleId)?.toResponse()
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)
    }

    private suspend fun ArticleEntity.toResponse(): ArticleResponse {
        val category = categoryRepository.findById(this.categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

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