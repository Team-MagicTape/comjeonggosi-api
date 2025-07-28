package com.comjeonggosi.infra.oauth2.data

import com.comjeonggosi.domain.user.domain.entity.UserEntity
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val oauth2User: OAuth2User,
     val user: UserEntity
) : OAuth2User by oauth2User