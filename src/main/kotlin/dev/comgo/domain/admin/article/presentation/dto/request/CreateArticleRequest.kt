package dev.comgo.domain.admin.article.presentation.dto.request

data class CreateArticleRequest(
    val title: String,
    val content: String,
    val categoryId: Long,
)