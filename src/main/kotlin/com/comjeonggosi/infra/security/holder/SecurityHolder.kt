package com.comjeonggosi.infra.security.holder

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.error.UserErrorCode
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import com.comjeonggosi.infra.oauth2.data.CustomOAuth2User
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityHolder(
    private val userRepository: UserRepository
) {
    suspend fun getUser(): UserEntity {
        val userId = findUserId()
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        return userRepository.findById(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
    }

    suspend fun getUserOrNull(): UserEntity? {
        val userId = findUserId()

        return userId?.let { userRepository.findById(it) }
    }

    private suspend fun findUserId(): Long? {
        return ReactiveSecurityContextHolder.getContext()
            .cast(org.springframework.security.core.context.SecurityContext::class.java)
            .mapNotNull { (it.authentication.principal as CustomOAuth2User).user.id }
            .awaitSingleOrNull()
    }
}