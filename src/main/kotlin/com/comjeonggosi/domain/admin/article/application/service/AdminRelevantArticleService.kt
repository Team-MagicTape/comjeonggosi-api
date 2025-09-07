package com.comjeonggosi.domain.admin.article.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.admin.article.presentation.dto.request.LinkArticleRequest
import com.comjeonggosi.domain.admin.article.presentation.dto.request.UnlinkArticleRequest
import com.comjeonggosi.domain.article.domain.entity.RelevantArticleEntity
import com.comjeonggosi.domain.article.domain.error.ArticleErrorCode
import com.comjeonggosi.domain.article.domain.error.RelevantArticleErrorCode
import com.comjeonggosi.domain.article.domain.repository.ArticleRepository
import com.comjeonggosi.domain.article.domain.repository.RelevantArticleRepository
import org.springframework.stereotype.Service

@Service
class AdminRelevantArticleService(
    private val articleRepository: ArticleRepository,
    private val relevantArticleRepository: RelevantArticleRepository,
) {
    suspend fun linkArticle(request: LinkArticleRequest) {
        if (isDuplicatedLink(request.fromArticleId, request.toArticleId))
            throw CustomException(RelevantArticleErrorCode.LINK_ALREADY_EXISTS)

        val fromArticle = articleRepository.findById(request.fromArticleId)
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)

        val toArticle = articleRepository.findById(request.toArticleId)
            ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)

        relevantArticleRepository.save(
            RelevantArticleEntity(
                fromArticleId = fromArticle.id!!,
                toArticleId = toArticle.id!!,
                isBefore = request.isBefore
            )
        )
    }

    suspend fun unlinkArticle(request: UnlinkArticleRequest) {
        val exist = relevantArticleRepository
            .findByFromArticleIdAndToArticleId(request.fromArticleId, request.toArticleId)
            ?: throw CustomException(RelevantArticleErrorCode.LINK_NOT_FOUND)

        relevantArticleRepository.delete(exist)
    }

    suspend fun isDuplicatedLink(fromArticleId: Long, toArticleId: Long): Boolean {
        relevantArticleRepository
            .findByFromArticleIdAndToArticleId(fromArticleId, toArticleId) ?: return false
        return true
    }
}