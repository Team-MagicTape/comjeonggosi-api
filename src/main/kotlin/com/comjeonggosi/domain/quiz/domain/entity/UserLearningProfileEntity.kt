package com.comjeonggosi.domain.quiz.domain.entity

import com.comjeonggosi.common.domain.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_learning_profiles")
data class UserLearningProfileEntity(
    @Id
    val id: Long? = null,

    @Column("user_id")
    val userId: Long,

    @Column("total_solved")
    val totalSolved: Int = 0,

    @Column("total_correct")
    val totalCorrect: Int = 0,

    @Column("streak_days")
    val streakDays: Int = 0,

    @Column("last_study_date")
    val lastStudyDate: Instant? = null,

    @Column("preferred_study_time")
    val preferredStudyTime: String? = null
) : BaseEntity()