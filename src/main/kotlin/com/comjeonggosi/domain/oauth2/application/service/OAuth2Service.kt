package com.comjeonggosi.domain.oauth2.application.service

import com.comjeonggosi.domain.oauth2.presentation.dto.request.GoogleLoginRequest
import com.comjeonggosi.domain.oauth2.presentation.dto.request.KakaoLoginRequest
import com.comjeonggosi.domain.oauth2.presentation.dto.request.NaverLoginRequest
import org.springframework.stereotype.Service

@Service
class OAuth2Service {
    suspend fun loginWithGoogle(request: GoogleLoginRequest): String {
        TODO("Implement Google login logic here")
    }

    suspend fun loginWithKakao(request: KakaoLoginRequest): String {
        TODO("Implement Kakao login logic here")
    }

    suspend fun loginWithNaver(request: NaverLoginRequest): String {
        TODO("Implement Naver login logic here")
    }

    suspend fun loginWithGithub(request: GoogleLoginRequest): String {
        TODO("Implement Apple login logic here")
    }
}