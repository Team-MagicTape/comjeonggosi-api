package com.comjeonggosi.domain.article.presentation.controller

import com.comjeonggosi.domain.article.application.service.ArticleService
import com.comjeonggosi.domain.article.presentation.dto.response.ArticleResponse
import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import com.comjeonggosi.infra.security.jwt.provider.JwtProvider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(ArticleController::class)
class ArticleControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var articleService: ArticleService

    @MockitoBean
    private lateinit var jwtProvider: JwtProvider

    @Test
    @WithMockUser(roles = ["USER"])
    @DisplayName("GET /articles/{articleId} : 게시글 단건 조회를 성공하고 200 OK와 데이터를 반환한다")
    fun getArticle() {
        // given
        val articleId = 1L
        val mockResponse = ArticleResponse(
            id = articleId,
            title = "테스트 제목",
            content = "테스트 내용",
            category = CategoryResponse(
                id = 10L, name = "데이터베이스",
                description = "데이터를 효율적으로 저장, 관리, 검색 및 활용하는 방법을 배우는 학문입니다"
            )
        )

        runBlocking {
            whenever(articleService.getArticle(articleId)).thenReturn(mockResponse)
        }

        // when & then
        webTestClient.get().uri("/articles/{articleId}", articleId)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("id").isEqualTo(articleId)
            .jsonPath("title").isEqualTo(mockResponse.title)
            .jsonPath("content").isEqualTo(mockResponse.content)
            .jsonPath("category.name").isEqualTo(mockResponse.category.name)
    }
}