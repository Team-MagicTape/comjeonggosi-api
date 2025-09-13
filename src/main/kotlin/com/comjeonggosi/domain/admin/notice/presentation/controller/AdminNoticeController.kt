package com.comjeonggosi.domain.admin.notice.presentation.controller

import com.comjeonggosi.domain.admin.notice.presentation.dto.request.CreateNoticeRequest
import com.comjeonggosi.domain.notice.application.service.NoticeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/notices")
class AdminNoticeController(
    private val noticeService: NoticeService
) {
    @PostMapping
    suspend fun create(@RequestBody request: CreateNoticeRequest) =
        noticeService.create(title = request.title, content = request.content)

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long) = noticeService.delete(id)
}
