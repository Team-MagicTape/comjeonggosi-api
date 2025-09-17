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
    ) = mapOf(
        "attachments" to listOf(
            mapOf(
                "color" to "danger",
                "title" to "[${activeProfile.uppercase()}] $errorCode",
                "text" to "${exception.message}",
                "fields" to listOf(
                    mapOf(
                        "title" to "Path",
                        "value" to path,
                        "short" to true
                    ),
                    mapOf(
                        "title" to "Exception",
                        "value" to exception.javaClass.simpleName,
                        "short" to true
                    )
                ) + additionalInfo.map { (key, value) ->
                    mapOf(
                        "title" to key,
                        "value" to value.toString(),
                        "short" to true
                    )
                },
                "ts" to (System.currentTimeMillis() / 1000)
            )
        )
    )
}