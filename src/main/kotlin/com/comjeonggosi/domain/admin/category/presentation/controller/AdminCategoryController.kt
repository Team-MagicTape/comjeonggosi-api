package com.comjeonggosi.domain.admin.category.presentation.controller

import com.comjeonggosi.domain.admin.category.application.service.AdminCategoryService
import com.comjeonggosi.domain.admin.category.presentation.dto.request.CreateCategoryRequest
import com.comjeonggosi.domain.admin.category.presentation.dto.request.UpdateCategoryRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/categories")
class AdminCategoryController(
    private val adminCategoryService: AdminCategoryService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createCategory(@RequestBody request: CreateCategoryRequest) =
        adminCategoryService.createCategory(request)

    @GetMapping
    suspend fun getCategories() = adminCategoryService.getCategories()

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updateCategory(
        @PathVariable categoryId: Long,
        @RequestBody request: UpdateCategoryRequest
    ) = adminCategoryService.updateCategory(categoryId, request)

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteCategory(@PathVariable categoryId: Long) = adminCategoryService.deleteCategory(categoryId)
}