package com.comjeonggosi.domain.admin.notice.presentation.dto.request

import jakarta.validation.constraints.NotBlank

data class CreateNoticeRequest(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val content: String,
)
