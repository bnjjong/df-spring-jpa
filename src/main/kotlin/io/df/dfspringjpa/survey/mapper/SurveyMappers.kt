package io.df.dfspringjpa.survey.mapper

import io.df.dfspringjpa.survey.api.QuestionCreateRequest
import io.df.dfspringjpa.survey.api.QuestionGroupCreateRequest
import io.df.dfspringjpa.survey.api.SurveyCreateRequest
import io.df.dfspringjpa.survey.domain.Question
import io.df.dfspringjpa.survey.domain.QuestionGroup
import io.df.dfspringjpa.survey.domain.Survey
import org.mapstruct.*

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
abstract class QuestionMapper {
    @Mappings(
        value = [
            Mapping(target = "id", ignore = true),
            Mapping(target = "group", ignore = true)
        ]
    )
    abstract fun toEntity(req: QuestionCreateRequest): Question
}

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = [QuestionMapper::class],
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
abstract class QuestionGroupMapper {
    @Mappings(
        value = [
            Mapping(target = "id", ignore = true),
            Mapping(target = "survey", ignore = true),
            Mapping(target = "questions", source = "questions")
        ]
    )
    protected abstract fun toEntityInternal(req: QuestionGroupCreateRequest): QuestionGroup

    fun toEntity(req: QuestionGroupCreateRequest, parent: Survey): QuestionGroup {
        val group = toEntityInternal(req)
        group.applySurvey(parent)
        group.questions.forEach { it.applyGroup(group)}
        return group
    }
}

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = [QuestionGroupMapper::class, QuestionMapper::class],
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
abstract class SurveyMapper {
    @Mappings(
        value = [
            Mapping(target = "id", ignore = true),
            Mapping(target = "groups", ignore = true)
        ]
    )
    protected abstract fun toEntityWithoutGroups(req: SurveyCreateRequest): Survey

    fun toEntity(req: SurveyCreateRequest, @Context groupMapper: QuestionGroupMapper): Survey {
        // delegate base mapping to MS
        val survey = toEntityWithoutGroups(req)
        // Groups are appended in @AfterMapping
        return survey
    }

    @AfterMapping
    protected fun after(
        @MappingTarget survey: Survey,
        req: SurveyCreateRequest,
        @Context groupMapper: QuestionGroupMapper
    ) {
        req.questionGroups.forEach { gReq ->
            val group = groupMapper.toEntity(gReq, survey)
            survey.addGroup(group)
        }
    }
}
