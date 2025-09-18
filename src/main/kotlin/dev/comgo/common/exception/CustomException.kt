package dev.comgo.common.exception

import dev.comgo.common.exception.error.CustomErrorCode
import dev.comgo.common.exception.response.ErrorResponse
import org.springframework.http.ResponseEntity

class CustomException(val error: CustomErrorCode, vararg args: Any?) : RuntimeException() {
    val status: Int = error.status.value()
    val code: String = (error as Enum<*>).name
    override val message = error.message.format(*args)

    fun toResponse(path: String) = ResponseEntity.status(status)
        .body(ErrorResponse(
            status = status,
            code = code,
            message = message,
            path = path
        ))
}