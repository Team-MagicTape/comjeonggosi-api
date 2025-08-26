package com.comjeonggosi.domain.article.presentation.controller

import com.comjeonggosi.domain.article.application.service.ArticleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService
) {
    @GetMapping
    suspend fun getArticles(@RequestParam(required = false) categoryId: Long?) = articleService.getArticles(categoryId)

    @GetMapping("/{articleId}")
    suspend fun getArticle(@PathVariable articleId: Long) = articleService.getArticle(articleId)
}