package dev.comgo.domain.quiz.domain.entity

import dev.comgo.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("quiz_review_schedules")
data class QuizReviewScheduleEntity(
    @Id
    val id: Long? = null,

    @Column("user_id")
    val userId: Long,

    @Column("quiz_id")
    val quizId: String,

    @Column("review_count")
    val reviewCount: Int = 0,

    @Column("last_reviewed_at")
    val lastReviewedAt: Instant? = null,

    @Column("next_review_at")
    val nextReviewAt: Instant,

    @Column("retention_score")
    val retentionScore: Double = 0.0,

    @Column("difficulty_adjustment")
    val difficultyAdjustment: Double = 1.0,

    @Column("is_mastered")
    val isMastered: Boolean = false
) : BaseEntity()