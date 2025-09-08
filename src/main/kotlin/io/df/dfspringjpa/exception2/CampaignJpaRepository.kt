package io.df.dfspringjpa.exception2

import org.springframework.data.jpa.repository.JpaRepository

interface CampaignJpaRepository : JpaRepository<CampaignEntity, String>
