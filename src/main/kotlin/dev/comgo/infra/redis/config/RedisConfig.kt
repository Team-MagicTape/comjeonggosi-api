package dev.comgo.infra.redis.config

import io.lettuce.core.ClientOptions
import io.lettuce.core.protocol.ProtocolVersion
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(private val redisProperties: RedisProperties) {
    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port).apply {
            redisProperties.password?.let { password = RedisPassword.of(it) }
        }

        val clientConfig = LettuceClientConfiguration.builder()
            .clientOptions(
                ClientOptions.builder()
                    .protocolVersion(ProtocolVersion.RESP2)
                    .build()
            )
            .build()

        return LettuceConnectionFactory(config, clientConfig).apply {
            afterPropertiesSet()
        }
    }

    @Bean
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<String, Any> {
        val context = RedisSerializationContext.newSerializationContext<String, Any>(StringRedisSerializer())
            .value(Jackson2JsonRedisSerializer(Any::class.java))
            .hashKey(StringRedisSerializer())
            .hashValue(Jackson2JsonRedisSerializer(Any::class.java))
            .build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory(), context)
    }
}