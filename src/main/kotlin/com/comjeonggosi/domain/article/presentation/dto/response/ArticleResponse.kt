package com.comjeonggosi.domain.article.presentation.dto.response

import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse

data class ArticleResponse(
    val id: Long,
    val title: String,
    val content: String,
    val category: CategoryResponse,
)