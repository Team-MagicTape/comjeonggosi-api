package dev.comgo.domain.admin.notice.presentation.controller

import dev.comgo.domain.admin.notice.presentation.dto.request.CreateNoticeRequest
import dev.comgo.domain.notice.application.service.NoticeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/notices")
class AdminNoticeController(
    private val noticeService: NoticeService
) {
    @PostMapping
    suspend fun create(@RequestBody request: CreateNoticeRequest) =
        noticeService.create(request)

    @DeleteMapping("/{noticeId}")
    suspend fun delete(@PathVariable noticeId: Long) = noticeService.delete(noticeId)
}
