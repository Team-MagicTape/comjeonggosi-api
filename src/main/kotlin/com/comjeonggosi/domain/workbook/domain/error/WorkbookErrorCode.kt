package com.comjeonggosi.domain.workbook.domain.error

import com.comjeonggosi.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class WorkbookErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    WORKBOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "Workbook not found"),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "Quiz not found"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),
}