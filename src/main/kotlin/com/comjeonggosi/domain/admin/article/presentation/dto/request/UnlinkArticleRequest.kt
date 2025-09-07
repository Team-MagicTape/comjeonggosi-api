package com.comjeonggosi.domain.admin.article.presentation.dto.request

data class UnlinkArticleRequest(
    val fromArticleId: Long,
    val toArticleId: Long
)
