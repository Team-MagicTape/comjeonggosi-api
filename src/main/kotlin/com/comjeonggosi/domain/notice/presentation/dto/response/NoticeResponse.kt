package com.comjeonggosi.domain.notice.presentation.dto.response

import java.time.Instant

data class NoticeResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Instant,
)
