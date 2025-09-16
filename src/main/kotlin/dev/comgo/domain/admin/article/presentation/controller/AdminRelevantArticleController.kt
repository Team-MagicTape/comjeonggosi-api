package dev.comgo.domain.admin.article.presentation.controller

import dev.comgo.domain.admin.article.application.service.AdminRelevantArticleService
import dev.comgo.domain.admin.article.presentation.dto.request.LinkArticleRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/articles/{articleId}/link")
class AdminRelevantArticleController(
    private val adminRelevantArticleService: AdminRelevantArticleService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun linkArticle(@PathVariable articleId: Long, @RequestBody request: LinkArticleRequest) =
        adminRelevantArticleService.linkArticle(request, articleId)

    @DeleteMapping("/{toId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun unlinkArticle(@PathVariable articleId: Long, @PathVariable toId: Long) =
        adminRelevantArticleService.unlinkArticle(toId, articleId)
}