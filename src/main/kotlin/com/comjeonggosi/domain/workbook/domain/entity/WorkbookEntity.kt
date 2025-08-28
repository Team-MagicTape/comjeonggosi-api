package com.comjeonggosi.domain.workbook.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("workbooks")
data class WorkbookEntity(
    @Id
    val id: Long? = null,
    val name: String,
    val description: String,
    val ownerId: Long,
    val deletedAt: Instant? = null,
) : BaseEntity()
