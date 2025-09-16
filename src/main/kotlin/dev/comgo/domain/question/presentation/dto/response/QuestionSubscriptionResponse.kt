package dev.comgo.domain.question.presentation.dto.response

data class QuestionSubscriptionResponse(
    val hour: Int,
    val categories: List<Category>,
    val email: String
) {
    data class Category(
        val id: Long,
        val name: String,
    )
}