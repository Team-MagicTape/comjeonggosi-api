package com.comjeonggosi.domain.quiz.domain.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "quizzes")
data class QuizDocument(
    @Id
    val id: String? = null,

    @Field("content")
    val content: String,

    @Field("answer")
    val answer: String,

    @Field("options")
    val options: List<String>,

    @Field("category_id")
    val categoryId: Long,

    @Field("deleted_at")
    val deletedAt: Instant? = null,
)