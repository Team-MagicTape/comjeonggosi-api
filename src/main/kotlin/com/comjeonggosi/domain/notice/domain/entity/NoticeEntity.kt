package com.comjeonggosi.domain.notice.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("notices")
data class NoticeEntity(
    @Id
    val id: Long? = null,
    val title: String,
    val content: String,
    val deletedAt: Instant? = null,
) : BaseEntity()
