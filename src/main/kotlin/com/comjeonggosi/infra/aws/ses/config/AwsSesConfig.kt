package com.comjeonggosi.infra.aws.ses.config

import com.comjeonggosi.infra.aws.config.AwsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesAsyncClient

@Configuration
class AwsSesConfig(
    private val awsProperties: AwsProperties
) {
    @Bean
    fun sesAsyncClient(): SesAsyncClient {
        val credentials = AwsBasicCredentials.create(
            awsProperties.ses.accessKey,
            awsProperties.ses.secretKey
        )

        return SesAsyncClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(awsProperties.region))
            .build()
    }
}