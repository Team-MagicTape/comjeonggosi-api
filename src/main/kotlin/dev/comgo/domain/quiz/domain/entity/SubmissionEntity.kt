package dev.comgo.domain.quiz.domain.entity

import dev.comgo.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("submissions")
data class SubmissionEntity(
    @Id
    val id: Long? = null,

    @Column("user_id")
    val userId: Long,

    @Column("quiz_id")
    val quizId: String,

    @Column("answer")
    val answer: String,

    @Column("is_corrected")
    val isCorrected: Boolean
) : BaseEntity()
