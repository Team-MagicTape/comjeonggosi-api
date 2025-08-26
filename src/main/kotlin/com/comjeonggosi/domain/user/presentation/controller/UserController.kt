package com.comjeonggosi.domain.user.presentation.controller

import com.comjeonggosi.domain.user.application.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/my")
    suspend fun getMyUser() = userService.getMyUser()
}
