package com.comjeonggosi.domain.admin.post.presentation.dto.request

import com.comjeonggosi.domain.post.domain.enums.PostCategory

data class CreatePostRequest(
    val title: String,
    val content: String,
    val category: PostCategory
)