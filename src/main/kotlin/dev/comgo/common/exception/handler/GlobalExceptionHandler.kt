package dev.comgo.common.exception.handler

import dev.comgo.common.exception.CustomException
import dev.comgo.common.exception.response.ErrorResponse
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.Hint
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import org.springframework.security.access.AccessDeniedException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        captureToSentry(ex, SentryLevel.WARNING)
        return createResponse(ex.status, ex.code, ex.message, exchange)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(
        ex: WebExchangeBindException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
            .ifEmpty { "입력값 검증 실패" }

        captureToSentry(ex, SentryLevel.INFO)
        return createResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", message, exchange)
    }

    @ExceptionHandler(value = [
        IllegalArgumentException::class,
        ServerWebInputException::class,
        AccessDeniedException::class,
        Exception::class
    ])
    fun handleGeneralException(
        ex: Exception,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val (status, code, message, level) = when (ex) {
            is IllegalArgumentException -> Quartet(
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                ex.message ?: "잘못된 인자",
                SentryLevel.INFO
            )
            is ServerWebInputException -> Quartet(
                HttpStatus.BAD_REQUEST,
                "INVALID_INPUT",
                "잘못된 입력",
                SentryLevel.INFO
            )
            is AccessDeniedException -> Quartet(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "접근 권한 없음",
                SentryLevel.WARNING
            )
            else -> Quartet(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "서버 오류",
                SentryLevel.ERROR
            )
        }

        captureToSentry(ex, level)
        return createResponse(status.value(), code, message, exchange)
    }

    private fun captureToSentry(throwable: Throwable, level: SentryLevel) {
        Sentry.captureException(throwable) { scope ->
            scope.level = level
        }
    }

    private fun createResponse(
        status: Int,
        code: String,
        message: String,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(status)
            .body(ErrorResponse(status, code, message, exchange.request.uri.path))
    }

    private data class Quartet<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}