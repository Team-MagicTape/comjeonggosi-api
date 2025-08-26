package com.comjeonggosi.domain.user.presentation.dto.response

import java.time.Instant

data class MyUserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val createdAt: Instant,
)