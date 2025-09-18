package dev.comgo.infra.security.holder

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.user.domain.entity.UserEntity
import dev.comgo.domain.user.domain.error.UserErrorCode
import dev.comgo.domain.user.domain.repository.UserRepository
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
            ?.principal as? Long ?: throw CustomException(UserErrorCode.USER_NOT_LOGGED_IN)
    }

    suspend fun getUser(): UserEntity {
        val userId = getUserId()
        return userRepository.findById(userId) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
    }

    suspend fun isAuthenticated(): Boolean {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication?.isAuthenticated == true }
            .defaultIfEmpty(false)
            .awaitSingleOrNull() ?: false
    }
}