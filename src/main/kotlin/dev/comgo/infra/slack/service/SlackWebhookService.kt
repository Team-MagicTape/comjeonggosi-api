package dev.comgo.infra.slack.service

import dev.comgo.infra.slack.config.SlackProperties
import dev.comgo.logger
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class SlackWebhookService(
    private val slackProperties: SlackProperties,
    @Value("\${spring.profiles.active:default}") private val activeProfile: String
) {
    private val log = logger()
    private val webClient = WebClient.builder().build()

    suspend fun sendError(
        exception: Throwable,
        errorCode: String,
        path: String,
        additionalInfo: Map<String, Any?> = emptyMap()
    ) {
        if (!slackProperties.webhook.enabled) {
            return
        }

        try {
            val payload = createPayload(exception, errorCode, path, additionalInfo)

            webClient.post()
                .uri(slackProperties.webhook.url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .awaitSingleOrNull()
        } catch (e: Exception) {
            log.error("Failed to send slack webhook", e)
        }
    }

    private fun createPayload(
        exception: Throwable,
        errorCode: String,
        path: String,
        additionalInfo: Map<String, Any?>
    ): Map<String, Any> {
        val stackTrace = exception.stackTraceToString()
        val truncatedStackTrace = if (stackTrace.length > 3000) {
            stackTrace.take(2800) + "\n... (truncated)"
        } else {
            stackTrace
        }

        val blocks = mutableListOf<Map<String, Any>>(
            mapOf(
                "type" to "header",
                "text" to mapOf(
                    "type" to "plain_text",
                    "text" to "[${activeProfile.uppercase()}] $errorCode"
                )
            ),
            mapOf(
                "type" to "section",
                "fields" to listOf(
                    mapOf(
                        "type" to "mrkdwn",
                        "text" to "*Path:*\n`$path`"
                    ),
                    mapOf(
                        "type" to "mrkdwn",
                        "text" to "*Exception:*\n`${exception.javaClass.simpleName}`"
                    )
                )
            )
        )

        if (exception.message != null) {
            blocks.add(
                mapOf(
                    "type" to "section",
                    "text" to mapOf(
                        "type" to "mrkdwn",
                        "text" to "*Message:*\n${exception.message}"
                    )
                )
            )
        }

        if (additionalInfo.isNotEmpty()) {
            blocks.add(
                mapOf(
                    "type" to "section",
                    "fields" to additionalInfo.map { (key, value) ->
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to "*$key:*\n$value"
                        )
                    }
                )
            )
        }

        blocks.add(
            mapOf(
                "type" to "divider"
            )
        )

        blocks.add(
            mapOf(
                "type" to "context",
                "elements" to listOf(
                    mapOf(
                        "type" to "mrkdwn",
                        "text" to "```$truncatedStackTrace```"
                    )
                )
            )
        )

        return mapOf(
            "blocks" to blocks,
            "attachments" to listOf(
                mapOf(
                    "color" to "danger",
                    "fallback" to "[${activeProfile.uppercase()}] $errorCode: ${exception.message ?: "No message"}"
                )
            )
        )
    }
}