package dev.comgo.domain.category.presentation.controller

import dev.comgo.domain.category.application.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService
) {
    @GetMapping
    suspend fun getCategories() = categoryService.getCategories()
}