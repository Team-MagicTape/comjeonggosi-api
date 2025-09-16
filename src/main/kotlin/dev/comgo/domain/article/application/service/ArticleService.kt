package dev.comgo.domain.article.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.article.application.helper.RelevantArticleResponseHelper
import dev.comgo.domain.article.domain.entity.ArticleEntity
import dev.comgo.domain.article.domain.error.ArticleErrorCode
import dev.comgo.domain.article.domain.repository.ArticleRepository
import dev.comgo.domain.article.presentation.dto.response.ArticleResponse
import dev.comgo.domain.category.domain.error.CategoryErrorCode
import dev.comgo.domain.category.domain.repository.CategoryRepository
import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository,
    private val relevantArticleResponseHelper: RelevantArticleResponseHelper
) {
    fun getSummarizedArticles(categoryId: Long?): Flow<ArticleResponse> {
        val articles = if (categoryId == null) {
            articleRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
        } else {
            articleRepository.findAllByDeletedAtIsNullAndCategoryIdOrderByCreatedAtDesc(categoryId)
        }
        return articles.map { it.toResponse(false) }
    }

    suspend fun getArticle(articleId: Long): ArticleResponse {
        return articleRepository.findById(articleId)?.toResponse(true)
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)
    }

    fun getArticles(categoryId: Long?): Flow<ArticleResponse> {
        val articles = if (categoryId == null) {
            articleRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
        } else {
            articleRepository.findAllByDeletedAtIsNullAndCategoryIdOrderByCreatedAtDesc(categoryId)
        }
        return articles.map { it.toResponse(true) }
    }

    private suspend fun ArticleEntity.toResponse(isDetail: Boolean): ArticleResponse {
        val category = categoryRepository.findById(categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        val (beforeArticles, afterArticles) = relevantArticleResponseHelper.mapRelevantArticles(this.id!!)

        val content = if (!isDetail) toSummarizedContent(content) else content

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

    private suspend fun toSummarizedContent(content: String): String {
        return content
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
                if (it.length > 20) {
                    it.take(20) + "..."
                } else {
                    it
                }
            }
    }
}