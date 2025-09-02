package io.df.dfspringjpa.repository

import io.df.dfspringjpa.domain.Survey
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveyRepository : JpaRepository<Survey, UUID>
