package io.df.dfspringjpa.exception2

import io.df.dfspringjpa.exception2.domain.CampaignEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CampaignJpaRepository : JpaRepository<CampaignEntity, String>
