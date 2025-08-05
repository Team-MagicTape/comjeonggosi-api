package com.comjeonggosi.domain.category.domain.error

import com.comjeonggosi.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class CategoryErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 카테고리입니다.")
}