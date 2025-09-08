package com.comjeonggosi.domain.admin.article.presentation.dto.request

data class LinkArticleRequest(
    val to: Long,
    val isBefore: Boolean,
)
