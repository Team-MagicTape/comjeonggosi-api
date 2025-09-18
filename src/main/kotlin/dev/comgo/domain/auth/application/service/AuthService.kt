package dev.comgo.domain.auth.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.auth.domain.error.AuthErrorCode
import dev.comgo.domain.user.domain.entity.UserEntity
import dev.comgo.domain.user.domain.error.UserErrorCode
import dev.comgo.domain.user.domain.repository.UserRepository
import dev.comgo.infra.cookie.config.CookieProperties
import dev.comgo.infra.security.jwt.enums.JwtType
import dev.comgo.infra.security.jwt.provider.JwtProvider
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
            ?: throw CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)

        if (!jwtProvider.validateToken(refreshToken)) {
            throw CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        val userId = jwtProvider.getUserId(refreshToken)
        val user = userRepository.findById(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        val newAccessToken = jwtProvider.generateToken(user.id!!, user.role, JwtType.ACCESS_TOKEN)

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
        val user = userRepository.findById(userId) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
        return userRepository.save(user.copy(lastLoginAt = Instant.now()))
    }

    private fun createCookie(name: String, value: String, maxAge: Duration): ResponseCookie {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(cookieProperties.secure)
            .sameSite(cookieProperties.sameSite)
            .maxAge(maxAge)
            .path("/")
//            .domain(cookieProperties.domain)
            .build()
    }
}