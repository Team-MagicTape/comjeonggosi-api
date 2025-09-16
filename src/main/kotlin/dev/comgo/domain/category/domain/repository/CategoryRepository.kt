package dev.comgo.domain.category.domain.repository

import dev.comgo.domain.category.domain.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CategoryRepository : CoroutineCrudRepository<CategoryEntity, Long> {
    fun findAllByDeletedAtIsNull(): Flow<CategoryEntity>
}