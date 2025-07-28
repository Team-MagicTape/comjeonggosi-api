package com.comjeonggosi.infra.oauth2.service

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import com.comjeonggosi.infra.oauth2.data.CustomOAuth2User
import com.comjeonggosi.infra.oauth2.data.OAuth2UserInfo
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
@Primary
class OAuth2UserService(
    private val userRepository: UserRepository
) : DefaultReactiveOAuth2UserService() {
    override fun loadUser(request: OAuth2UserRequest): Mono<OAuth2User> {
        return super.loadUser(request)
            .flatMap { oauth2User ->
                val provider = request.clientRegistration.registrationId
                val userInfo = extractUserInfo(provider, oauth2User.attributes)

                mono {
                    val user = ensureUser(
                        provider = provider,
                        providerId = userInfo.providerId,
                        email = userInfo.email,
                        nickname = userInfo.nickname,
                        profileImageUrl = userInfo.profileImageUrl
                    )
                    CustomOAuth2User(oauth2User, user)
                }
            }
    }

    private fun extractUserInfo(provider: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (provider) {
            "google" -> extractGoogleUserInfo(attributes)
            "naver" -> extractNaverUserInfo(attributes)
            "kakao" -> extractKakaoUserInfo(attributes)
            "github" -> extractGithubUserInfo(attributes)
            else -> throw IllegalArgumentException("Unknown provider: $provider")
        }
    }

    private fun extractGoogleUserInfo(attributes: Map<String, Any>): OAuth2UserInfo {
        return OAuth2UserInfo(
            providerId = attributes["sub"].toString(),
            email = attributes["email"].toString(),
            nickname = attributes["name"].toString(),
            profileImageUrl = attributes["picture"]?.toString()
        )
    }

    private fun extractNaverUserInfo(attributes: Map<String, Any>): OAuth2UserInfo {
        val response = attributes["response"] as Map<*, *>
        return OAuth2UserInfo(
            providerId = response["id"].toString(),
            email = response["email"].toString(),
            nickname = response["nickname"].toString(),
            profileImageUrl = response["profile_image"]?.toString()
        )
    }

    private fun extractKakaoUserInfo(attributes: Map<String, Any>): OAuth2UserInfo {
        val kakaoAccount = attributes["kakao_account"] as Map<*, *>
        val profile = kakaoAccount["profile"] as Map<*, *>

        return OAuth2UserInfo(
            providerId = attributes["id"].toString(),
            email = kakaoAccount["email"].toString(),
            nickname = profile["nickname"].toString(),
            profileImageUrl = profile["profile_image_url"]?.toString()
        )
    }

    private fun extractGithubUserInfo(attributes: Map<String, Any>): OAuth2UserInfo {
        return OAuth2UserInfo(
            providerId = attributes["id"].toString(),
            email = attributes["email"]?.toString() ?: "",
            nickname = attributes["login"].toString(),
            profileImageUrl = attributes["avatar_url"]?.toString()
        )
    }

    private suspend fun ensureUser(
        provider: String,
        providerId: String,
        email: String,
        nickname: String,
        profileImageUrl: String?
    ): UserEntity {
        val existingUser = userRepository.findByProviderAndProviderId(provider, providerId)
        if (existingUser != null) {
            return existingUser
        }

        return userRepository.save(
            UserEntity(
                provider = provider,
                providerId = providerId,
                email = email,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                lastLoginAt = Instant.now()
            )
        )
    }
}