package dev.comgo.common.exception

import dev.comgo.common.exception.error.CustomErrorCode

class CustomException(val error: CustomErrorCode, vararg args: Any?) : RuntimeException() {
    val status: Int = error.status.value()
    val code: String = (error as Enum<*>).name
    override val message = error.message.format(*args)
}