package dev.comgo.domain.article.presentation.controller

import dev.comgo.domain.article.application.service.ArticleService
import dev.comgo.domain.article.presentation.dto.response.ArticleResponse
import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
import dev.comgo.infra.security.jwt.provider.JwtProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient

// ./gradlew asciidoctor 실행 후 build/docs/asciidoc/index.html 파일 생성
@WebFluxTest(ArticleController::class)
@AutoConfigureRestDocs
class ArticleControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @RegisterExtension
    val restDocumentation: RestDocumentationExtension = RestDocumentationExtension()

    @MockitoBean
    private lateinit var articleService: ArticleService

    @MockitoBean
    private lateinit var jwtProvider: JwtProvider

    @BeforeEach
    fun setUp(applicationContext: ApplicationContext?, restDocumentation: RestDocumentationContextProvider?) {
        this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext!!).configureClient()
            .filter(documentationConfiguration(restDocumentation))
            .build()
    }

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
            .consumeWith(
                document(
                    "articles/get-article",
                    pathParameters(
                        parameterWithName("articleId").description("게시글 ID")
                    ),
                    responseFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("content").description("내용"),
                        fieldWithPath("category").description("카테고리"),
                        fieldWithPath("category.id").description("카테고리 ID"),
                        fieldWithPath("category.name").description("카테고리 이름"),
                        fieldWithPath("category.description").description("카테고리 설명")
                    )
                )
            )
    }

    @Test
    @WithMockUser(roles = ["USER"])
    @DisplayName("GET /articles : 게시글 목록 조회를 성공하고 200 OK와 데이터를 반환한다")
    fun getArticles() {
        // given
        val mockResponse = flowOf(
            ArticleResponse(
                id = 1L,
                title = "테스트 제목 1",
                content = "테스트 내용 1",
                category = CategoryResponse(id = 10L, name = "데이터베이스", description = "DB 설명")
            ),
            ArticleResponse(
                id = 2L,
                title = "테스트 제목 2",
                content = "테스트 내용 2",
                category = CategoryResponse(id = 11L, name = "알고리즘", description = "알고리즘 설명")
            )
        )

        runBlocking {
            whenever(articleService.getArticles(null)).thenReturn(mockResponse)
        }

        // when & then
        webTestClient.get().uri("/articles")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(1L)
            .jsonPath("$[0].title").isEqualTo("테스트 제목 1")
            .jsonPath("$[1].id").isEqualTo(2L)
            .jsonPath("$[1].title").isEqualTo("테스트 제목 2")
            .consumeWith(
                document(
                    "articles/get-articles",
                    responseFields(
                        fieldWithPath("[].id").description("게시글 ID"),
                        fieldWithPath("[].title").description("제목"),
                        fieldWithPath("[].content").description("내용"),
                        fieldWithPath("[].category").description("카테고리"),
                        fieldWithPath("[].category.id").description("카테고리 ID"),
                        fieldWithPath("[].category.name").description("카테고리 이름"),
                        fieldWithPath("[].category.description").description("카테고리 설명")
                    )
                )
            )
    }
}