package io.df.dfspringjpa.exception2.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.df.dfspringjpa.exception2.BillingError
import io.df.dfspringjpa.exception2.BillingGateway
import io.df.dfspringjpa.exception2.Campaign
import io.df.dfspringjpa.exception2.CampaignRepo
import io.df.dfspringjpa.exception2.Receipt

class ChargeService(
    private val repo: CampaignRepo,
    private val billing: BillingGateway
) {
    fun charge(campaignId: String, amount: Long): Either<BillingError, Receipt> = either {
        val c = repo.find(campaignId).bind()  // NotFound면 여기서 즉시 Left로 반환
        ensure(c.remain >= amount) {
            BillingError.NotEnoughBudget(campaignId, need = amount, remain = c.remain)
        }
        ensure(checkDailyQuota(c, amount)) {
            BillingError.QuotaExceeded(campaignId, quota = c.dailyQuota)
        }
        // 다운스트림 I/O → 타입드 에러로 이미 래핑된 Port
        val receipt = billing.issueReceipt(campaignId, amount).bind()
        repo.updateRemain(campaignId, c.remain - amount).bind()
        receipt
    }

    private fun checkDailyQuota(c: Campaign, amount: Long): Boolean =
        amount <= c.dailyQuota
}
