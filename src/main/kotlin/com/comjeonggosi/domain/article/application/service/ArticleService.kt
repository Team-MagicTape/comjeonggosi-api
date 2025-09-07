package com.comjeonggosi.domain.article.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.admin.article.application.service.RelevantArticleResponseHelper
import com.comjeonggosi.domain.article.domain.entity.ArticleEntity
import com.comjeonggosi.domain.article.domain.error.ArticleErrorCode
import com.comjeonggosi.domain.article.domain.repository.ArticleRepository
import com.comjeonggosi.domain.article.presentation.dto.response.ArticleResponse
import com.comjeonggosi.domain.category.domain.error.CategoryErrorCode
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository,
    private val relevantArticleResponseHelper: RelevantArticleResponseHelper
) {
    fun getArticles(categoryId: Long?): Flow<ArticleResponse> {
        val articles = if (categoryId == null) {
            articleRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
        } else {
            articleRepository.findAllByDeletedAtIsNullAndCategoryIdOrderByCreatedAtDesc(categoryId)
        }
        return articles.map { it.toResponse() }
    }

    suspend fun getArticle(articleId: Long): ArticleResponse {
        return articleRepository.findById(articleId)?.toDetailResponse()
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)
    }

    private suspend fun ArticleEntity.toResponse(): ArticleResponse {
        val category = categoryRepository.findById(categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        val (beforeArticles, afterArticles) = relevantArticleResponseHelper.mapRelevantArticles(this.id!!)

        val content = content
            .replace(Regex("#{1,6}\\s*"), "")
            .replace(Regex("\\*{1,3}|_{1,3}"), "")
            .replace(Regex("\\[([^]]+)]\\([^)]+\\)"), "$1")
            .replace(Regex("```[\\s\\S]*?```"), "")
            .replace(Regex("`([^`]+)`"), "$1")
            .replace(Regex("^[-*+]\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^\\d+\\.\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^>\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("\\n{2,}"), " ")
            .trim()
            .let {
                if (it.length > 20) { it.take(20) + "..." } else { it }
            }

        return ArticleResponse(
            id = id,
            title = title,
            content = content,
            category = CategoryResponse(
                id = category.id!!,
                name = category.name,
                description = category.description
            ),
            beforeArticles = beforeArticles,
            afterArticles = afterArticles
        )
    }

    private suspend fun ArticleEntity.toDetailResponse(): ArticleResponse {
        val category = categoryRepository.findById(categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        val (beforeArticles, afterArticles) = relevantArticleResponseHelper.mapRelevantArticles(this.id!!)

        return ArticleResponse(
            id = id,
            title = title,
            content = content,
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