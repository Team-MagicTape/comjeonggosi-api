package com.comjeonggosi.domain.article.presentation.controller

import com.comjeonggosi.domain.article.application.service.ArticleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService
) {
    @GetMapping
    suspend fun getArticles() = articleService.getArticles()

    @GetMapping("/{articleId}")
    suspend fun getArticle(@PathVariable articleId: Long) = articleService.getArticle(articleId)
}