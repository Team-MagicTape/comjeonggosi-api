package com.comjeonggosi.domain.post.application.service

import com.comjeonggosi.domain.post.domain.entity.PostEntity
import com.comjeonggosi.domain.post.domain.repository.PostRepository
import com.comjeonggosi.domain.admin.post.presentation.dto.response.PostResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
) {
    suspend fun getAllPosts(): Flow<PostResponse> {
        return postRepository.findAll().map { it.toResponse() }
    }

    suspend fun getPost(id: Long): PostResponse {
        return postRepository.findById(id)?.toResponse() ?: throw IllegalArgumentException("")
    }

    private fun PostEntity.toResponse(): PostResponse {
        return PostResponse(
            id = this.id!!,
            title = this.title,
            content = this.content,
        )
    }
}