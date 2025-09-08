package io.df.dfspringjpa.exception2

import arrow.core.Either
import arrow.core.left

class CampaignService(private val repo: CampaignRepo) {
    data class CreateCommand(val id: String, val remain: Long, val dailyQuota: Long)

    fun create(cmd: CreateCommand): Either<BillingError, Unit> {
        // validation
        if (cmd.id.isBlank()) return BillingError.InvalidSegmentRule("id must not be blank").left()
        if (cmd.remain < 0) return BillingError.InvalidSegmentRule("remain must be >= 0").left()
        if (cmd.dailyQuota <= 0) return BillingError.InvalidSegmentRule("dailyQuota must be > 0").left()

        val c = Campaign(id = cmd.id, remain = cmd.remain, dailyQuota = cmd.dailyQuota)
        return repo.create(c)
    }
}
