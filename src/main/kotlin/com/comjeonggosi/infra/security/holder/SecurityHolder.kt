package com.comjeonggosi.infra.security.holder

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import com.comjeonggosi.infra.oauth2.data.CustomOAuth2User
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityHolder(
    private val userRepository: UserRepository
) {
    suspend fun getUser(): UserEntity {
        val userId = ReactiveSecurityContextHolder.getContext()
            .cast(org.springframework.security.core.context.SecurityContext::class.java)
            .map { (it.authentication.principal as CustomOAuth2User).user.id }
            .awaitSingle()

        return userRepository.findById(userId!!)
            ?: throw IllegalStateException("User not found with id: $userId")
    }
}