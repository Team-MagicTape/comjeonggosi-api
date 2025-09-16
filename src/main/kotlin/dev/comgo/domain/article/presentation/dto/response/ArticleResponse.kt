package dev.comgo.domain.article.presentation.dto.response

import dev.comgo.domain.category.presentation.dto.response.CategoryResponse

data class ArticleResponse(
    val id: Long,
    val title: String,
    val content: String,
    val category: CategoryResponse,
    val beforeArticles: List<RelevantArticleResponse>,
    val afterArticles: List<RelevantArticleResponse>,
)