package dev.comgo.infra.oauth2.data

data class OAuth2UserInfo(
    val providerId: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?
)