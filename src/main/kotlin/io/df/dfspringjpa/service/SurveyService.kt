package io.df.dfspringjpa.service

import io.df.dfspringjpa.api.SurveyCreateRequest
import io.df.dfspringjpa.domain.Question
import io.df.dfspringjpa.domain.QuestionGroup
import io.df.dfspringjpa.domain.Survey
import io.df.dfspringjpa.repository.SurveyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SurveyService(
    private val repository: SurveyRepository
) {
    @Transactional
    fun createSurvey(req: SurveyCreateRequest): UUID {
        val survey = Survey.create(title = req.title)

        // 계층 구성
        req.questionGroups.forEach { g ->
            val group = QuestionGroup.create(name = g.name)
            survey.addGroup(group)

            g.questions.forEach { q ->
                val question = Question.create(
                    text = q.text,
                    type = q.type
                )
                group.addQuestion(question)
            }
        }

        // cascade = CascadeType.ALL 로 루트만 저장해도 하위까지 저장
        val saved = repository.save(survey)
        return requireNotNull(saved.id)
    }
}