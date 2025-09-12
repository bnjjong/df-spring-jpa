package io.df.dfspringjpa.sketches.repo

import io.df.dfspringjpa.sketches.domain.Audience
import io.df.dfspringjpa.sketches.domain.AudienceTheta
import org.springframework.data.jpa.repository.JpaRepository

interface AudienceThetaRepository: JpaRepository<AudienceTheta, String> {

}