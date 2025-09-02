package io.df.dfspringjpa.api

import io.df.dfspringjpa.service.SurveyService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/api/v1/surveys")
class SurveyController(
    private val surveyService: SurveyService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: SurveyCreateRequest): SurveyCreateResponse {
        val surveyId = surveyService.createSurvey(req)
        return SurveyCreateResponse(id = surveyId)
    }
}

data class SurveyCreateRequest(
    @field:NotBlank
    val title: String,

    @field:Size(min = 1, message = "questionGroups 는 최소 1개 이상이어야 합니다.")
    val questionGroups: List<QuestionGroupCreateRequest>
)

data class QuestionGroupCreateRequest(
    @field:NotBlank
    val name: String,

    @field:Size(min = 1, message = "questions 는 최소 1개 이상이어야 합니다.")
    val questions: List<QuestionCreateRequest>
)

data class QuestionCreateRequest(
    @field:NotBlank
    val text: String,

    // 예: SHORT_TEXT, LONG_TEXT, SINGLE_CHOICE, MULTIPLE_CHOICE 등
    @field:NotBlank
    val type: String
)

data class SurveyCreateResponse(
    val id: UUID
)