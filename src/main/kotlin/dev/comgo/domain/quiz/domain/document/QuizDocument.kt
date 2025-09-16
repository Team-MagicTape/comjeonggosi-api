package dev.comgo.domain.quiz.domain.document

import dev.comgo.domain.quiz.domain.enums.QuizType
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

    @Field("article_id")
    val articleId: Long? = null,

    @Field("type")
    val type: QuizType = QuizType.MULTIPLE_CHOICE,

    @Field("difficulty")
    val difficulty: Int = 3,

    @Field("created_at")
    val createdAt: Instant = Instant.now(),

    @Field("updated_at")
    val updatedAt: Instant = Instant.now(),

    @Field("solve_count")
    val solveCount: Int = 0,

    @Field("correct_rate")
    val correctRate: Double = 0.0,
)