package com.comjeonggosi.infra.security.jwt.enums

enum class JwtType(
    val expiration: Long
) {
    ACCESS_TOKEN(3600000),
    REFRESH_TOKEN(2592000000)
}