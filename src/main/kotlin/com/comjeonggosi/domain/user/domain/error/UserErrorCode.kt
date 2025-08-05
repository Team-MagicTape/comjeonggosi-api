package com.comjeonggosi.domain.user.domain.error

import com.comjeonggosi.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
}