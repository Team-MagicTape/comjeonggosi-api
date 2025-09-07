package com.comjeonggosi.domain.admin.article.presentation.dto.request

data class LinkArticleRequest(
    val fromArticleId: Long,
    val toArticleId: Long,
    val isBefore: Boolean,
)
