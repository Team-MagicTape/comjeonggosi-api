package com.comjeonggosi.infra.security.holder

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.repository.UserRepository
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
            .map { it.authentication.principal as Long }
            .awaitSingle()

        return userRepository.findById(userId)
            ?: throw IllegalStateException("User not found with id: $userId")
    }
}