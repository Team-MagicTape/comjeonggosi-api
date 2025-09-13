package com.comjeonggosi.domain.notice.presentation.controller

import com.comjeonggosi.domain.notice.application.service.NoticeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    @GetMapping
    fun getAll() = noticeService.getAll()

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: Long) = noticeService.getById(id)
}
