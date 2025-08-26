package com.comjeonggosi.infra.security.holder

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityHolder(
    private val userRepository: UserRepository
) {
    suspend fun getUserId(): Long {
        return ReactiveSecurityContextHolder.getContext()
            .awaitSingle()
            .authentication
            ?.principal as? Long ?: throw IllegalStateException("No authenticated user")
    }

    suspend fun getUser(): UserEntity {
        val userId = getUserId()
        return userRepository.findById(userId) ?: throw IllegalStateException("User not found")
    }

    suspend fun isAuthenticated(): Boolean {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication?.isAuthenticated == true }
            .defaultIfEmpty(false)
            .awaitSingleOrNull() ?: false
    }
}