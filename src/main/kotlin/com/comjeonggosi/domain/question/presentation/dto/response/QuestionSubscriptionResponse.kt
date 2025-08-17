package com.comjeonggosi.domain.question.presentation.dto.response

data class QuestionSubscriptionResponse(
    val hour: Int,
    val categories: List<Category>
) {
    data class Category(
        val id: Long,
        val name: String,
    )
}