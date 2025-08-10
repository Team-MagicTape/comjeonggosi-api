package com.comjeonggosi.infra.security.holder

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.infra.oauth2.data.CustomOAuth2User
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityHolder {
    suspend fun getUser(): UserEntity {
        return ReactiveSecurityContextHolder.getContext()
            .awaitSingle()
            .let { (it.authentication.principal as CustomOAuth2User).user }
    }

    suspend fun isAuthenticated(): Boolean {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication?.isAuthenticated == true }
            .defaultIfEmpty(false)
            .awaitSingleOrNull() ?: false
    }
}