package com.comjeonggosi.domain.article.presentation.controller

import com.comjeonggosi.domain.article.application.service.ArticleService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ArticleGraphqlController(
    private val articleService: ArticleService
) {
    @QueryMapping
    fun getArticles(@Argument categoryId: Long?) = articleService.getArticles(categoryId)

    @QueryMapping
    suspend fun getArticle(@Argument("id") articleId: Long) = articleService.getArticle(articleId)
}