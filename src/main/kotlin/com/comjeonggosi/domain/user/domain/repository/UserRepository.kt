package com.comjeonggosi.domain.user.domain.repository

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.infra.oauth2.enums.OAuth2Provider
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<UserEntity, Long> {
    suspend fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): UserEntity?
}