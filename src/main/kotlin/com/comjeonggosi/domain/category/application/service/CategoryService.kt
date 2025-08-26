package com.comjeonggosi.domain.category.application.service

import com.comjeonggosi.domain.category.domain.entity.CategoryEntity
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    suspend fun getCategories(): Flow<CategoryResponse> {
        return categoryRepository.findAllByDeletedAtIsNull().map { it.toResponse() }
    }

    private fun CategoryEntity.toResponse() =
        CategoryResponse(
            id = this.id!!,
            name = this.name,
            description = this.description,
        )
}