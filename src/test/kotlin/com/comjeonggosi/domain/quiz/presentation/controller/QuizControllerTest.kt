package com.comjeonggosi.domain.quiz.presentation.controller

import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import com.comjeonggosi.domain.quiz.application.service.QuizService
import com.comjeonggosi.domain.quiz.presentation.dto.request.SolveQuizRequest
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizResponse
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizSubmissionResponse
import com.comjeonggosi.domain.quiz.presentation.dto.response.SolveQuizResponse
import com.comjeonggosi.infra.security.jwt.provider.JwtProvider
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
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(QuizController::class)
@AutoConfigureRestDocs
class QuizControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var quizService: QuizService

    @RegisterExtension
    val restDocumentation: RestDocumentationExtension = RestDocumentationExtension()

    @MockitoBean
    private lateinit var jwtProvider: JwtProvider

    @BeforeEach
    fun setUp(applicationContext: ApplicationContext?, restDocumentation: RestDocumentationContextProvider?) {
        this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext!!).configureClient()
            .filter(documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @WithMockUser
    fun getRandomQuiz() {
        // given
        val quizId = "232437990"
        val quizResponse = QuizResponse(
            id = quizId,
            content = "This is a quiz.",
            answer = "answer",
            options = listOf("a", "b", "c"),
            category = CategoryResponse(1L, "category", "description")
        )

        runBlocking {
            whenever(quizService.getRandomQuiz(1)).thenReturn(quizResponse)
        }

        // when & then
        webTestClient.get().uri("/quizzes?categoryId=1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("id").isEqualTo(quizId)
            .jsonPath("answer").isEqualTo(quizResponse.answer)
            .jsonPath("content").isEqualTo(quizResponse.content)
            .jsonPath("category.name").isEqualTo(quizResponse.category.name)
            .jsonPath("options").isEqualTo(quizResponse.options)
            .consumeWith(
                document(
                    "quizzes/get-random-quiz",
                    queryParameters(
                        parameterWithName("categoryId").description("카테고리 ID")
                    ),
                    responseFields(
                        fieldWithPath("id").description("퀴즈 ID, 랜덤한 퀴즈가 1개 반환"),
                        fieldWithPath("content").description("내용"),
                        fieldWithPath("answer").description("정답"),
                        fieldWithPath("options").description("정답 외의 옵션"),
                        fieldWithPath("category").description("카테고리"),
                        fieldWithPath("category.id").description("카테고리 ID"),
                        fieldWithPath("category.name").description("카테고리 이름"),
                        fieldWithPath("category.description").description("카테고리 설명")
                    )
                )
            )
    }

    @Test
    @WithMockUser
    @DisplayName("POST /quizzes/{quizId}/solve : 퀴즈 풀고 200 OK와 데이터를 반환한다")
    fun solve() {
        val quizId = "232437990"
        val solveRequest = SolveQuizRequest(answer = "O")
        val solveResponse = SolveQuizResponse(isCorrect = false, answer = "answer")

        runBlocking {
            whenever(quizService.solve(quizId, solveRequest))
                .thenReturn(solveResponse)
        }

        webTestClient.mutateWith(csrf()).post()
            .uri("/quizzes/{quizId}/solve", quizId)
            .bodyValue(solveRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.isCorrect").isEqualTo(false)
            .jsonPath("$.answer").isEqualTo("answer")
            .consumeWith(
                document(
                    "quizzes/solve",
                    pathParameters(
                        parameterWithName("quizId").description("퀴즈 ID")
                    ),
                    requestFields(
                        fieldWithPath("answer").description("사용자 답안")
                    ),
                    responseFields(
                        fieldWithPath("isCorrect").description("정답 여부"),
                        fieldWithPath("answer").description("실제 정답")
                    )
                )
            )
    }

    @Test
    @WithMockUser
    @DisplayName("GET /quizzes/submissions/my : 자신이 풀었던 퀴즈 목록을 조회합니다.")
    fun getMySubmissions() {
        val quizId = "232437990"
        val quizResponse = QuizResponse(
            id = quizId,
            content = "This is a quiz.",
            answer = "answer",
            options = listOf("a", "b", "c"),
            category = CategoryResponse(1L, "category", "description")
        )
        val submission = QuizSubmissionResponse(
            quiz = quizResponse,
            isCorrected = true,
            userAnswer = "X"
        )

        runBlocking {
            whenever(quizService.getMySubmissions(true)).thenReturn(flowOf(submission))
        }

        // when & then
        webTestClient.get().uri("/quizzes/submissions/my?isCorrected=true")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].quiz.id").isEqualTo(submission.quiz.id)
            .jsonPath("$[0].isCorrected").isEqualTo(submission.isCorrected)
            .jsonPath("$[0].userAnswer").isEqualTo(submission.userAnswer)
            .consumeWith(
                document(
                    "quizzes/get-my-submissions",
                    queryParameters(
                        parameterWithName("isCorrected").description("정답 여부")
                    ),
                    responseFields(
                        fieldWithPath("[].isCorrected").description("정답 여부"),
                        fieldWithPath("[].userAnswer").description("유저가 제출했던 답"),
                        fieldWithPath("[].quiz.id").description("퀴즈 ID, 랜덤한 퀴즈가 1개 반환"),
                        fieldWithPath("[].quiz.content").description("내용"),
                        fieldWithPath("[].quiz.answer").description("정답"),
                        fieldWithPath("[].quiz.options").description("정답 외의 옵션"),
                        fieldWithPath("[].quiz.category").description("카테고리"),
                        fieldWithPath("[].quiz.category.id").description("카테고리 ID"),
                        fieldWithPath("[].quiz.category.name").description("카테고리 이름"),
                        fieldWithPath("[].quiz.category.description").description("카테고리 설명")
                    )
                )
            )
    }
}