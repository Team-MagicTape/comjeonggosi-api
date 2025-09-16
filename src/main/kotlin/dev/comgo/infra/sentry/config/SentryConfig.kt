package dev.comgo.infra.sentry.config

import io.sentry.spring.jakarta.EnableSentry
import org.springframework.context.annotation.Configuration

@EnableSentry
@Configuration
class SentryConfig