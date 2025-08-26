package com.comjeonggosi.infra.elasticsearch.config

import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

@Configuration
class ElasticsearchConfig(
    private val elasticsearchProperties: ElasticsearchProperties
) : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo(elasticsearchProperties.uris.first())
            .withBasicAuth(elasticsearchProperties.username, elasticsearchProperties.password)
            .build()
    }
}