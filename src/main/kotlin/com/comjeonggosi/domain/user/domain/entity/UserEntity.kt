package com.comjeonggosi.domain.user.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("users")
data class UserEntity(
    @Id
    val id: Long? = null,
    val provider: String,
    val providerId: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String? = null,
    val lastLoginAt: Instant? = null,
) : BaseEntity()