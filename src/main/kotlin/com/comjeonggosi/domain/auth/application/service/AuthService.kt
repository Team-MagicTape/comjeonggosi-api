package com.comjeonggosi.domain.auth.application.service

import com.comjeonggosi.domain.auth.presentation.dto.request.RefreshRequest
import com.comjeonggosi.infra.security.jwt.data.JwtPayload
import com.comjeonggosi.infra.security.jwt.provider.JwtProvider
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
) {
    suspend fun refreshToken(request: RefreshRequest): JwtPayload {
        if (!jwtProvider.validateToken(request.refreshToken))
            throw IllegalArgumentException("Invalid refresh token")

        val userId = jwtProvider.getUserId(request.refreshToken)
        return jwtProvider.generateToken(userId)
    }
}