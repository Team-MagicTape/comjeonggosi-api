package com.comjeonggosi.domain.post.domain.repository

import com.comjeonggosi.domain.post.domain.entity.PostEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : CoroutineCrudRepository<PostEntity, Long> {
}