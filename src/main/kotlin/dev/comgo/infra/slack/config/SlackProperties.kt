package dev.comgo.infra.slack.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "slack")
data class SlackProperties(
    val webhook: Webhook
) {
    data class Webhook(
        val url: String,
        val enabled: Boolean
    )
}