package com.comjeonggosi.domain.category.presentation.controller

import com.comjeonggosi.domain.category.application.service.CategoryService
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