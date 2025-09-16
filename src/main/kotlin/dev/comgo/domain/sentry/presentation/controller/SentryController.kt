package dev.comgo.domain.sentry.presentation.controller

import io.sentry.Sentry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sentry")
class SentryController {
    @GetMapping("/test")
    suspend fun testSentry(): String {
        Sentry.captureException(RuntimeException("This is a test exception for Sentry"))
        return "Sentry test exception has been sent."
    }
}