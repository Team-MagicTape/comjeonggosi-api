package com.comjeonggosi.domain.admin.category.application.service

import com.comjeonggosi.domain.admin.category.presentation.dto.request.CreateCategoryRequest
import com.comjeonggosi.domain.admin.category.presentation.dto.request.UpdateCategoryRequest
import com.comjeonggosi.domain.category.domain.entity.CategoryEntity
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AdminCategoryService(
    private val categoryRepository: CategoryRepository
) {
    suspend fun createCategory(request: CreateCategoryRequest) {
        categoryRepository.save(
            CategoryEntity(
                name = request.name,
                description = request.description
            )
        )
    }

    suspend fun getCategories(): Flow<CategoryResponse> {
        return categoryRepository.findAllByDeletedAtIsNull().map { it.toResponse() }
    }

    suspend fun updateCategory(categoryId: Long, request: UpdateCategoryRequest) {
        val category = categoryRepository.findById(categoryId) ?: throw IllegalArgumentException("category not found")

        categoryRepository.save(
            category.copy(
                name = request.name ?: category.name,
                description = request.description ?: category.description
            )
        )
    }

    suspend fun deleteCategory(categoryId: Long) {
        val category = categoryRepository.findById(categoryId) ?: throw IllegalArgumentException("category not found")

        categoryRepository.save(
            category.copy(
                deletedAt = Instant.now()
            )
        )
    }

    private fun CategoryEntity.toResponse() =
        CategoryResponse(
            id = this.id!!,
            name = this.name,
            description = this.description,
        )
}