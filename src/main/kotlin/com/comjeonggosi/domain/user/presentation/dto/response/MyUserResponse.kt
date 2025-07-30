package com.comjeonggosi.domain.user.presentation.dto.response

data class MyUserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?
)