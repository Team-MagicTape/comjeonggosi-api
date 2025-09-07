package com.comjeonggosi.domain.admin.article.presentation.controller

import com.comjeonggosi.domain.admin.article.application.service.AdminRelevantArticleService
import com.comjeonggosi.domain.admin.article.presentation.dto.request.LinkArticleRequest
import com.comjeonggosi.domain.admin.article.presentation.dto.request.UnlinkArticleRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/articles/link")
class AdminRelevantArticleController(
    private val adminRelevantArticleService: AdminRelevantArticleService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun linkArticle(@RequestBody request: LinkArticleRequest) =
        adminRelevantArticleService.linkArticle(request)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun unlinkArticle(@RequestBody request: UnlinkArticleRequest) =
        adminRelevantArticleService.unlinkArticle(request)
}