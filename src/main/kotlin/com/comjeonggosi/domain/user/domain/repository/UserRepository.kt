package com.comjeonggosi.domain.user.domain.repository

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<UserEntity, Long> {
    suspend fun findByProviderAndProviderId(provider: String, providerId: String): UserEntity?
    suspend fun findByEmail(email: String): UserEntity?
}