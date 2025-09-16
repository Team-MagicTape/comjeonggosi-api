package dev.comgo.infra.cookie.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cookie")
data class CookieProperties(
    val secure: Boolean,
    val sameSite: String,
    val domain: String?
)