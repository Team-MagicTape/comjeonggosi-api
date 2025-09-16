package dev.comgo.domain.category.presentation.controller

import dev.comgo.domain.article.presentation.dto.response.ArticleResponse
import dev.comgo.domain.category.application.service.CategoryService
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class CategoryGraphQLController(
    private val categoryService: CategoryService
) {
    @SchemaMapping(typeName = "Article", field = "category")
    suspend fun getCategory(article: ArticleResponse) = categoryService.getCategory(article.category.id)
}