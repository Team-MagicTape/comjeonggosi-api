package com.comjeonggosi.infra.aws.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws")
data class AwsProperties(
    val region: String,
    val ses: Ses,
) {
    data class Ses(
        val source: String,
        val accessKey: String,
        val secretKey: String
    )
}