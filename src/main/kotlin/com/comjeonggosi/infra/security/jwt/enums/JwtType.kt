package com.comjeonggosi.infra.security.jwt.enums

enum class JwtType(
    val expiration: Long
) {
    ACCESS(3600000),
    REFRESH(2592000000)
}