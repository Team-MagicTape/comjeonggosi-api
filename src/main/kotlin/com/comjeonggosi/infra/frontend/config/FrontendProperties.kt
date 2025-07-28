package com.comjeonggosi.infra.frontend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("frontend")
data class FrontendProperties(
    val baseUrl: String,
    val secure: Boolean
)