package com.comjeonggosi.infra.oauth2.enums

import com.comjeonggosi.infra.oauth2.data.OAuth2UserInfo


enum class OAuth2Provider(
    val extractorStrategy: OAuth2UserInfoExtractor
) {
    GOOGLE({ attributes ->
        OAuth2UserInfo(
            providerId = attributes["sub"].toString(),
            email = attributes["email"].toString(),
            nickname = attributes["name"].toString(),
            profileImageUrl = attributes["picture"]?.toString()
        )
    }),

    NAVER({ attributes ->
        val response = attributes["response"] as Map<*, *>
        OAuth2UserInfo(
            providerId = response["id"].toString(),
            email = response["email"].toString(),
            nickname = response["nickname"].toString(),
            profileImageUrl = response["profile_image"]?.toString()
        )
    }),

    KAKAO({ attributes ->
        val kakaoAccount = attributes["kakao_account"] as Map<*, *>
        val profile = kakaoAccount["profile"] as Map<*, *>
        OAuth2UserInfo(
            providerId = attributes["id"].toString(),
            email = kakaoAccount["email"].toString(),
            nickname = profile["nickname"].toString(),
            profileImageUrl = profile["profile_image_url"]?.toString()
        )
    }),

    GITHUB({ attributes ->
        OAuth2UserInfo(
            providerId = attributes["id"].toString(),
            email = attributes["email"]?.toString() ?: "",
            nickname = attributes["login"].toString(),
            profileImageUrl = attributes["avatar_url"]?.toString()
        )
    });

    fun extractUserInfo(attributes: Map<String, Any>): OAuth2UserInfo {
        return extractorStrategy.extract(attributes)
    }

    fun interface OAuth2UserInfoExtractor {
        fun extract(attributes: Map<String, Any>): OAuth2UserInfo
    }
}