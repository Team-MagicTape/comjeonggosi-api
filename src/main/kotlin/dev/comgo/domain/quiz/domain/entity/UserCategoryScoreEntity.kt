package dev.comgo.domain.quiz.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("user_category_scores")
data class UserCategoryScoreEntity(
    @Id
    val id: Long? = null,

    @Column("profile_id")
    val profileId: Long,

    @Column("category_id")
    val categoryId: Long,

    @Column("score")
    val score: Double
)