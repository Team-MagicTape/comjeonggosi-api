package com.comjeonggosi.domain.admin.question.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.admin.question.presentation.dto.request.CreateQuestionRequest
import com.comjeonggosi.domain.admin.question.presentation.dto.request.UpdateQuestionRequest
import com.comjeonggosi.domain.category.domain.error.CategoryErrorCode
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.question.domain.entity.QuestionEntity
import com.comjeonggosi.domain.question.domain.error.QuestionErrorCode
import com.comjeonggosi.domain.question.domain.repository.QuestionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminQuestionService(
    private val categoryRepository: CategoryRepository,
    private val questionRepository: QuestionRepository
) {
    @Transactional
    suspend fun createQuestion(request: CreateQuestionRequest) {
        val category = categoryRepository.findById(request.categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        if (questionRepository.existsByCategoryIdAndDay(category.id!!, request.day))
            throw CustomException(QuestionErrorCode.QUESTION_ALREADY_EXISTS)

        questionRepository.save(QuestionEntity(
            categoryId = category.id,
            day = request.day,
            title = request.title,
            content = request.content,
            answer = request.answer
        ))
    }

    suspend fun updateQuestion(questionId: Long, request: UpdateQuestionRequest) {
        val question = questionRepository.findById(questionId)
            ?: throw CustomException(QuestionErrorCode.QUESTION_NOT_FOUND)

        questionRepository.save(question.copy(
            title = request.title ?: question.title,
            content = request.content ?: question.content,
            answer = request.answer ?: question.answer
        ))
    }
}