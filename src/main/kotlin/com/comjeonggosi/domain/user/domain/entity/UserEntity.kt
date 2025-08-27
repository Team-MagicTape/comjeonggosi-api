package com.comjeonggosi.domain.user.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import com.comjeonggosi.domain.user.domain.enums.UserRole
import com.comjeonggosi.infra.oauth2.enums.OAuth2Provider
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("users")
data class UserEntity(
    @Id
    val id: Long? = null,
    val provider: OAuth2Provider,
    val providerId: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String? = null,
    val role: UserRole = UserRole.USER,
    val lastLoginAt: Instant? = null,
) : BaseEntity()