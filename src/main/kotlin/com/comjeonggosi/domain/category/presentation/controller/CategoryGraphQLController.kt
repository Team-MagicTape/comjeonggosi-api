package com.comjeonggosi.domain.category.presentation.controller

import com.comjeonggosi.domain.article.presentation.dto.response.ArticleResponse
import com.comjeonggosi.domain.category.application.service.CategoryService
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class CategoryGraphQLController(
    private val categoryService: CategoryService
) {
    @SchemaMapping(typeName = "Article", field = "category")
    suspend fun getCategory(article: ArticleResponse) = categoryService.getCategory(article.category.id)
}