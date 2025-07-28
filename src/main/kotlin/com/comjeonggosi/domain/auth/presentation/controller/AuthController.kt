package com.comjeonggosi.domain.auth.presentation.controller

import com.comjeonggosi.domain.auth.application.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun refreshToken(exchange: ServerWebExchange) = authService.refreshToken(exchange)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(exchange: ServerWebExchange) = authService.logout(exchange)
}