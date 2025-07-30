package com.comjeonggosi.domain.admin.post.service

import com.comjeonggosi.domain.admin.post.presentation.dto.request.CreatePostRequest
import com.comjeonggosi.domain.admin.post.presentation.dto.request.UpdatePostRequest
import com.comjeonggosi.domain.post.domain.entity.PostEntity
import com.comjeonggosi.domain.post.domain.repository.PostRepository
import com.comjeonggosi.infra.security.holder.SecurityHolder
import org.springframework.stereotype.Service

@Service
class AdminPostService(
    private val postRepository: PostRepository,
    private val securityHolder: SecurityHolder
) {
    suspend fun createPost(request: CreatePostRequest) {
        val me = securityHolder.getUser()

        val post = PostEntity(
            title = request.title,
            content = request.content,
            authorId = me.id!!,
            category = request.category,
        )
        postRepository.save(post)
    }

    suspend fun updatePost(id: Long, request: UpdatePostRequest) {
        val post = postRepository.findById(id) ?: throw IllegalArgumentException("post not found")

        request.title?.let { post.title = it }
        request.content?.let { post.content = it }

        postRepository.save(post)
    }

    suspend fun deletePost(id: Long) {
        val post = postRepository.findById(id) ?: throw IllegalArgumentException("post not found")

        post.isDeleted = true

        postRepository.delete(post)
    }
}