package com.comjeonggosi.domain.article.application.helper

import com.comjeonggosi.domain.article.domain.entity.RelevantArticleEntity
import com.comjeonggosi.domain.article.domain.repository.ArticleRepository
import com.comjeonggosi.domain.article.domain.repository.RelevantArticleRepository
import com.comjeonggosi.domain.article.presentation.dto.response.RelevantArticleResponse
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class RelevantArticleResponseHelper(
    private val articleRepository: ArticleRepository,
    private val relevantArticleRepository: RelevantArticleRepository
) {
    private suspend fun findRelevantArticles(fromArticleId: Long): List<RelevantArticleEntity> {
        return relevantArticleRepository.findAllByFromArticleId(fromArticleId).toList()
    }

    suspend fun mapRelevantArticles(fromArticleId: Long): Pair<List<RelevantArticleResponse>, List<RelevantArticleResponse>> {
        val relevantArticles = findRelevantArticles(fromArticleId)

        val toArticleIds = relevantArticles.mapNotNull { it.toArticleId }
        val articlesMap = articleRepository.findAllByIdInAndDeletedAtIsNull(toArticleIds)
            .associateBy { it.id }

        fun mapToResponse(isBefore: Boolean) =
            relevantArticles.filter { it.isBefore == isBefore }
                .map {
                    val toArticle = articlesMap[it.toArticleId]!!
                    RelevantArticleResponse(it.id!!, toArticle.title)
                }

        return mapToResponse(true) to mapToResponse(false)
    }
}