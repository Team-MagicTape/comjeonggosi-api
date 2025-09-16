package dev.comgo.domain.user.domain.repository

import dev.comgo.domain.user.domain.entity.UserEntity
import dev.comgo.infra.oauth2.enums.OAuth2Provider
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<UserEntity, Long> {
    suspend fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): UserEntity?
}