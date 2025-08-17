package com.comjeonggosi.infra.email.service

import com.comjeonggosi.infra.aws.config.AwsProperties
import com.comjeonggosi.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.model.*

@Service
class EmailService(
    private val sesAsyncClient: SesAsyncClient,
    private val awsProperties: AwsProperties,
) {
    private val log = logger()
    
    suspend fun sendEmail(to: String, subject: String, body: String) {
        val request = SendEmailRequest.builder()
            .source(awsProperties.ses.source)
            .destination(Destination.builder().toAddresses(to).build())
            .message(
                Message.builder()
                    .subject(Content.builder().data(subject).build())
                    .body(Body.builder().html(Content.builder().data(body).build()).build())
                    .build()
            )
            .build()

        runCatching {
            sesAsyncClient.sendEmail(request).await()
        }.onFailure { error ->
            log.error("Email send failed for: $to", error)
        }
    }
}