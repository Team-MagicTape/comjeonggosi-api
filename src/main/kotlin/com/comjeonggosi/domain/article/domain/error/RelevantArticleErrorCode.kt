package com.comjeonggosi.domain.article.domain.error

import com.comjeonggosi.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class RelevantArticleErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    LINK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 연결입니다."),
    LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "관계를 찾을 수 없습니다.")
}