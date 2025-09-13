package com.comjeonggosi.domain.notice.domain.error

import com.comjeonggosi.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class NoticeErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다."),
}
