package dev.comgo.domain.admin.article.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.admin.article.presentation.dto.request.CreateArticleRequest
import dev.comgo.domain.admin.article.presentation.dto.request.UpdateArticleRequest
import dev.comgo.domain.article.application.helper.RelevantArticleResponseHelper
import dev.comgo.domain.article.domain.entity.ArticleEntity
import dev.comgo.domain.article.domain.error.ArticleErrorCode
import dev.comgo.domain.article.domain.repository.ArticleRepository
import dev.comgo.domain.article.presentation.dto.response.ArticleResponse
import dev.comgo.domain.category.domain.error.CategoryErrorCode
import dev.comgo.domain.category.domain.repository.CategoryRepository
import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
import dev.comgo.infra.security.holder.SecurityHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AdminArticleService(
    private val articleRepository: ArticleRepository,
    private val securityHolder: SecurityHolder,
    private val categoryRepository: CategoryRepository,
    private val relevantArticleResponseHelper: RelevantArticleResponseHelper
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

    fun getArticles(categoryId: Long?): Flow<ArticleResponse> {
        val articles = if (categoryId == null) {
            articleRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
        } else {
            articleRepository.findAllByDeletedAtIsNullAndCategoryIdOrderByCreatedAtDesc(categoryId)
        }
        return articles.map { it.toResponse() }
    }

    suspend fun getArticle(articleId: Long): ArticleResponse {
        return articleRepository.findById(articleId)?.toResponse()
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)
    }

    private suspend fun ArticleEntity.toResponse(): ArticleResponse {
        val category = categoryRepository.findById(this.categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        val (beforeArticles, afterArticles) = relevantArticleResponseHelper.mapRelevantArticles(this.id!!)

        return ArticleResponse(
            id = this.id,
            title = this.title,
            content = this.content,
            category = CategoryResponse(
                id = category.id!!,
                name = category.name,
                description = category.description
            ),
            beforeArticles = beforeArticles,
            afterArticles = afterArticles
        )
    }
}