package com.comjeonggosi.domain.admin.article.service

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

        val article = ArticleEntity(
            title = request.title,
            content = request.content,
            authorId = me.id!!,
            categoryId = request.categoryId,
        )
        articleRepository.save(article)
    }

    suspend fun updateArticle(articleId: Long, request: UpdateArticleRequest) {
        val post = articleRepository.findById(articleId) ?: throw IllegalArgumentException("article not found")

        request.title?.let { post.title = it }
        request.content?.let { post.content = it }

        articleRepository.save(post)
    }

    suspend fun deleteArticle(articleId: Long) {
        val post = articleRepository.findById(articleId) ?: throw IllegalArgumentException("article not found")

        post.isDeleted = true
    }
}