package com.comjeonggosi.infra.oauth2.service

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import com.comjeonggosi.infra.oauth2.data.CustomOAuth2User
import com.comjeonggosi.infra.oauth2.enums.OAuth2Provider
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
                val provider = OAuth2Provider.valueOf(request.clientRegistration.registrationId.uppercase())
                val userInfo = provider.extractUserInfo(oauth2User.attributes)

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

    private suspend fun ensureUser(
        provider: OAuth2Provider,
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
                role = "USER",
                lastLoginAt = Instant.now()
            )
        )
    }
}