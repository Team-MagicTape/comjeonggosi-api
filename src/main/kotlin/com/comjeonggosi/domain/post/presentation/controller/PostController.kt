package com.comjeonggosi.domain.post.presentation.controller

import com.comjeonggosi.domain.post.application.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllPosts() = postService.getAllPosts()

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getPost(@PathVariable postId: Long) = postService.getPost(postId)
}