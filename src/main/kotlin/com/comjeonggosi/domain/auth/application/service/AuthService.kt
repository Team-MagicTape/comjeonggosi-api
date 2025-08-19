package com.comjeonggosi.domain.auth.application.service

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import com.comjeonggosi.infra.cookie.config.CookieProperties
import com.comjeonggosi.infra.security.jwt.enums.JwtType
import com.comjeonggosi.infra.security.jwt.provider.JwtProvider
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.time.Duration
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val cookieProperties: CookieProperties
) {
    suspend fun refreshToken(exchange: ServerWebExchange) {
        val refreshToken = exchange.request.cookies["refreshToken"]?.first()?.value
            ?: throw IllegalArgumentException("Refresh token not found")

        if (!jwtProvider.validateToken(refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }

        val userId = jwtProvider.getUserId(refreshToken)
        val newAccessToken = jwtProvider.generateToken(userId, JwtType.ACCESS_TOKEN)

        val accessTokenCookie = createCookie("accessToken", newAccessToken, Duration.ofHours(1))

        exchange.response.addCookie(accessTokenCookie)
    }

    suspend fun logout(exchange: ServerWebExchange) {
        val accessTokenCookie = createCookie("accessToken", "", Duration.ZERO)
        val refreshTokenCookie = createCookie("refreshToken", "", Duration.ZERO)

        exchange.response.addCookie(accessTokenCookie)
        exchange.response.addCookie(refreshTokenCookie)
    }

    suspend fun updateLastLoginAt(userId: Long): UserEntity {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")
        return userRepository.save(user.copy(lastLoginAt = Instant.now()))
    }

    private fun createCookie(name: String, value: String, maxAge: Duration): ResponseCookie {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(cookieProperties.secure)
            .sameSite(cookieProperties.sameSite)
            .maxAge(maxAge)
            .path("/")
            .domain(cookieProperties.domain)
            .build()
    }
}