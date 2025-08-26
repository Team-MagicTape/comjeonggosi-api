package com.comjeonggosi.common.exception.error

import org.springframework.http.HttpStatus

interface CustomErrorCode {
    val status: HttpStatus
    val message: String
}
