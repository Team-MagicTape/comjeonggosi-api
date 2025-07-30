package com.comjeonggosi.domain.user.application.service

import com.comjeonggosi.domain.user.presentation.dto.response.MyUserResponse
import com.comjeonggosi.infra.security.holder.SecurityHolder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val securityHolder: SecurityHolder
) {
    suspend fun getMyUser(): MyUserResponse {
        val user = securityHolder.getUser()

        return MyUserResponse(
            id = user.id!!,
            email = user.email,
            nickname = user.nickname,
            profileImageUrl = user.profileImageUrl,
        )
    }
}
