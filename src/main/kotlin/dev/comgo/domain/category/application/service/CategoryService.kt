package dev.comgo.domain.category.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.category.domain.entity.CategoryEntity
import dev.comgo.domain.category.domain.error.CategoryErrorCode
import dev.comgo.domain.category.domain.repository.CategoryRepository
import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    fun getCategories(): Flow<CategoryResponse> {
        return categoryRepository.findAllByDeletedAtIsNull().map { it.toResponse() }
    }

    suspend fun getCategory(categoryId: Long): CategoryResponse {
        return categoryRepository.findById(categoryId)?.toResponse()
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)
    }

    private fun CategoryEntity.toResponse() =
        CategoryResponse(
            id = this.id!!,
            name = this.name,
            description = this.description,
        )
}