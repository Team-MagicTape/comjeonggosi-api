package dev.comgo.domain.user.application.service

import dev.comgo.domain.user.presentation.dto.response.MyUserResponse
import dev.comgo.infra.security.holder.SecurityHolder
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
            createdAt = user.createdAt,
        )
    }
}
