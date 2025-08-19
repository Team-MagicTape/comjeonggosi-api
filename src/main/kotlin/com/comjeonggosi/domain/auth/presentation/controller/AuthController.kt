package com.comjeonggosi.domain.auth.presentation.controller

import com.comjeonggosi.domain.auth.application.service.AuthService
import com.comjeonggosi.domain.auth.presentation.dto.request.RefreshRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun refreshToken(@RequestBody request: RefreshRequest) = authService.refreshToken(request)
}