package io.df.dfspringjpa.sketches.repo

import io.df.dfspringjpa.sketches.domain.Audience
import org.springframework.data.jpa.repository.JpaRepository

interface AudienceRepository: JpaRepository<Audience, Long> {

}