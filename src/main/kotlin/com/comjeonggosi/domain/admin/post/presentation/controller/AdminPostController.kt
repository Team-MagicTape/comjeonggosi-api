package com.comjeonggosi.domain.admin.post.presentation.controller

import com.comjeonggosi.domain.admin.post.presentation.dto.request.CreatePostRequest
import com.comjeonggosi.domain.admin.post.presentation.dto.request.UpdatePostRequest
import com.comjeonggosi.domain.admin.post.service.AdminPostService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/posts")
class AdminPostController(
    private val adminPostService: AdminPostService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createPost(@RequestBody request: CreatePostRequest) = adminPostService.createPost(request)

    @PatchMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updatePost(@PathVariable postId: Long, @RequestBody request: UpdatePostRequest)
    = adminPostService.updatePost(postId, request)

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deletePost(@PathVariable postId: Long) = adminPostService.deletePost(postId)
}