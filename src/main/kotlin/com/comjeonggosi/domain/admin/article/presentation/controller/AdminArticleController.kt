package com.comjeonggosi.domain.admin.article.presentation.controller

import com.comjeonggosi.domain.admin.article.application.service.AdminArticleService
import com.comjeonggosi.domain.admin.article.presentation.dto.request.CreateArticleRequest
import com.comjeonggosi.domain.admin.article.presentation.dto.request.UpdateArticleRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/articles")
class AdminArticleController(
    private val adminArticleService: AdminArticleService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createArticle(@RequestBody request: CreateArticleRequest) = adminArticleService.createArticle(request)

    @PatchMapping("/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updateArticle(@PathVariable articleId: Long, @RequestBody request: UpdateArticleRequest) =
        adminArticleService.updateArticle(articleId, request)

    @DeleteMapping("/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteArticle(@PathVariable articleId: Long) = adminArticleService.deleteArticle(articleId)

    @GetMapping
    suspend fun getArticles() = adminArticleService.getArticles()

    @GetMapping("/{articleId}")
    suspend fun getArticle(@PathVariable articleId: Long) = adminArticleService.getArticle(articleId)
}