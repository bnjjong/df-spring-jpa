package io.df.dfspringjpa.survey.repository

import io.df.dfspringjpa.survey.domain.Survey
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveyRepository : JpaRepository<Survey, UUID>
