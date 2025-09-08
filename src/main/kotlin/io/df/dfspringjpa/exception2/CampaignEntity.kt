package io.df.dfspringjpa.exception2

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "campaigns")
class CampaignEntity(
    @Id
    @Column(name = "id", nullable = false, length = 64)
    var id: String = "",

    @Column(name = "remain", nullable = false)
    var remain: Long = 0L,

    @Column(name = "daily_quota", nullable = false)
    var dailyQuota: Long = 0L,
)
