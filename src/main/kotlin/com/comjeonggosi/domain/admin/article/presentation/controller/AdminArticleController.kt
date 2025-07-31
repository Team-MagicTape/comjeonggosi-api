package com.comjeonggosi.domain.admin.article.presentation.controller

import com.comjeonggosi.domain.admin.article.presentation.dto.request.CreateArticleRequest
import com.comjeonggosi.domain.admin.article.presentation.dto.request.UpdateArticleRequest
import com.comjeonggosi.domain.admin.article.service.AdminArticleService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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
    suspend fun updateArticle(@PathVariable articleId: Long, @RequestBody request: UpdateArticleRequest)
    = adminArticleService.updateArticle(articleId, request)

    @DeleteMapping("/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteArticle(@PathVariable articleId: Long) = adminArticleService.deleteArticle(articleId)
}