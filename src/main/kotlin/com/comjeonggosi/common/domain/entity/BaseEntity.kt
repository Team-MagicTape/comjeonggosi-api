package com.comjeonggosi.common.domain.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import java.time.Instant

abstract class BaseEntity {
    @CreatedDate
    @Column("created_at")
    var createdAt: Instant = Instant.now()
        protected set

    @LastModifiedDate
    @Column("updated_at")
    var updatedAt: Instant = Instant.now()
        protected set
}