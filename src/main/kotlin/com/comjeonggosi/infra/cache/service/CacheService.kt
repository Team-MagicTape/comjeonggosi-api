package com.comjeonggosi.infra.cache.service

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class CacheService(
    private val redisTemplate: ReactiveStringRedisTemplate
) {
    suspend fun get(key: String): String? {
        return redisTemplate.opsForValue().get(key).awaitSingleOrNull()
    }
    
    suspend fun set(key: String, value: String, ttl: Duration) {
        redisTemplate.opsForValue().set(key, value, ttl).awaitSingleOrNull()
    }
}