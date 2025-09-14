package com.comjeonggosi.common.exception.error

import org.springframework.http.HttpStatus

enum class GeneralErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
}
