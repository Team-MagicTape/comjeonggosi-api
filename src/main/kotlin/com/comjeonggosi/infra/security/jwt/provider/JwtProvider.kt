package com.comjeonggosi.infra.security.jwt.provider

import com.comjeonggosi.infra.security.jwt.config.JwtProperties
import com.comjeonggosi.infra.security.jwt.data.JwtPayload
import com.comjeonggosi.infra.security.jwt.enums.JwtType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateToken(userId: Long): JwtPayload {
        return JwtPayload(
            accessToken = generateToken(userId, JwtType.ACCESS),
            refreshToken = generateToken(userId, JwtType.REFRESH)
        )
    }

    private fun generateToken(userId: Long, type: JwtType): String {
        val now = Instant.now()

        return Jwts.builder()
            .header()
            .type(type.name)
            .and()
            .subject(userId.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(type.expiration, ChronoUnit.MILLIS)))
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserId(token: String): Long {
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        return claims.subject.toLong()
    }
}