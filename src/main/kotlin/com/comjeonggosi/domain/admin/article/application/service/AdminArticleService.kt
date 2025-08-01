package com.comjeonggosi.domain.admin.article.application.service

import com.comjeonggosi.domain.admin.article.presentation.dto.request.CreateArticleRequest
import com.comjeonggosi.domain.admin.article.presentation.dto.request.UpdateArticleRequest
import com.comjeonggosi.domain.article.domain.entity.ArticleEntity
import com.comjeonggosi.domain.article.domain.repository.ArticleRepository
import com.comjeonggosi.infra.security.holder.SecurityHolder
import org.springframework.stereotype.Service

@Service
class AdminArticleService(
    private val articleRepository: ArticleRepository,
    private val securityHolder: SecurityHolder
) {
    suspend fun createArticle(request: CreateArticleRequest) {
        val me = securityHolder.getUser()

        articleRepository.save(ArticleEntity(
            title = request.title,
            content = request.content,
            authorId = me.id!!,
            categoryId = request.categoryId,
        ))
    }

    suspend fun updateArticle(articleId: Long, request: UpdateArticleRequest) {
        val article = articleRepository.findById(articleId) ?: throw IllegalArgumentException("article not found")

        articleRepository.save(article.copy(
            title = request.title ?: article.title,
            content = request.content ?: article.content
        ))
    }

    suspend fun deleteArticle(articleId: Long) {
        val article = articleRepository.findById(articleId) ?: throw IllegalArgumentException("article not found")

        articleRepository.save(article.copy(
            isDeleted = true
        ))
    }
}