package dev.comgo.domain.admin.category.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.admin.category.presentation.dto.request.CreateCategoryRequest
import dev.comgo.domain.admin.category.presentation.dto.request.UpdateCategoryRequest
import dev.comgo.domain.category.domain.entity.CategoryEntity
import dev.comgo.domain.category.domain.error.CategoryErrorCode
import dev.comgo.domain.category.domain.repository.CategoryRepository
import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
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
        val category = categoryRepository.findById(categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        categoryRepository.save(
            category.copy(
                name = request.name ?: category.name,
                description = request.description ?: category.description
            )
        )
    }

    suspend fun deleteCategory(categoryId: Long) {
        val category = categoryRepository.findById(categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

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