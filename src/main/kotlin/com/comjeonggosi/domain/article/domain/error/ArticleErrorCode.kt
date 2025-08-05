package com.comjeonggosi.domain.article.domain.error

import com.comjeonggosi.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class ArticleErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 아티클입니다.")
}