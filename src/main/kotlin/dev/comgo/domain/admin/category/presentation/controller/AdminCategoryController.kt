package dev.comgo.domain.admin.category.presentation.controller

import dev.comgo.domain.admin.category.application.service.AdminCategoryService
import dev.comgo.domain.admin.category.presentation.dto.request.CreateCategoryRequest
import dev.comgo.domain.admin.category.presentation.dto.request.UpdateCategoryRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

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