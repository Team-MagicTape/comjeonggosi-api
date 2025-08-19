package com.comjeonggosi.infra.security.jwt.data

data class JwtPayload(
    val accessToken: String,
    val refreshToken: String
)