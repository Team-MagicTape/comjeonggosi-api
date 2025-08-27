package com.comjeonggosi.infra.oauth2.handler

import com.comjeonggosi.domain.auth.application.service.AuthService
import com.comjeonggosi.infra.cookie.config.CookieProperties
import com.comjeonggosi.infra.frontend.config.FrontendProperties
import com.comjeonggosi.infra.oauth2.data.CustomOAuth2User
import com.comjeonggosi.infra.security.jwt.enums.JwtType
import com.comjeonggosi.infra.security.jwt.provider.JwtProvider
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class OAuth2SuccessHandler(
    private val jwtProvider: JwtProvider,
    private val authService: AuthService,
    private val frontendProperties: FrontendProperties,
    private val cookieProperties: CookieProperties
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val oauth2User = authentication.principal as CustomOAuth2User
        val userId = oauth2User.user.id!!

        runBlocking {
            authService.updateLastLoginAt(userId)
        }

        val role = oauth2User.user.role
        val accessToken = jwtProvider.generateToken(userId, role, JwtType.ACCESS_TOKEN)
        val refreshToken = jwtProvider.generateToken(userId, role, JwtType.REFRESH_TOKEN)

        val response = webFilterExchange.exchange.response

        val accessTokenCookie = createCookie("accessToken", accessToken, Duration.ofHours(1))
        val refreshTokenCookie = createCookie("refreshToken", refreshToken, Duration.ofDays(30))

        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        val redirectUrl = UriComponentsBuilder
            .fromUriString(frontendProperties.baseUrl)
            .path("/auth/success")
            .build()
            .toUri()

        response.statusCode = HttpStatus.FOUND
        response.headers.location = redirectUrl

        return response.setComplete()
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