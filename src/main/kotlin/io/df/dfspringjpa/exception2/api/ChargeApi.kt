package io.df.dfspringjpa.exception2.api

import arrow.core.Either
import io.df.dfspringjpa.exception2.BillingClient
import io.df.dfspringjpa.exception2.BillingError
import io.df.dfspringjpa.exception2.BillingGateway
import io.df.dfspringjpa.exception2.CampaignRepo
import io.df.dfspringjpa.exception2.service.CampaignService
import io.df.dfspringjpa.exception2.service.ChargeService
import io.df.dfspringjpa.exception2.Dao
import io.df.dfspringjpa.exception2.FakeBillingClient
import io.df.dfspringjpa.exception2.JdbcCampaignRepo
import io.df.dfspringjpa.exception2.Receipt
import io.df.dfspringjpa.exception2.RestBillingGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// --- API DTOs ---

data class ChargeRequest(val amount: Long)

data class ReceiptResponse(
    val campaignId: String,
    val amount: Long,
    val txId: String
)

data class CreateCampaignRequest(val id: String, val remain: Long, val dailyQuota: Long)

// --- Controller ---

@RestController
@RequestMapping("/api/exception2")
class ChargeController(
    private val service: ChargeService,
    private val campaignService: CampaignService,
) {
    @PostMapping("/campaigns/{id}/charge")
    fun charge(
        @PathVariable("id") id: String,
        @RequestBody req: ChargeRequest
    ): ResponseEntity<Any> {
        val result: Either<BillingError, Receipt> = service.charge(id, req.amount)
        return when (result) {
            is Either.Right -> ResponseEntity.ok(
                ReceiptResponse(
                    campaignId = result.value.campaignId,
                    amount = result.value.amount,
                    txId = result.value.txId
                )
            )
            is Either.Left -> when (val e = result.value) {
                is BillingError.NotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "NOT_FOUND", "id" to e.id))
                is BillingError.NotEnoughBudget -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "error" to "NOT_ENOUGH_BUDGET",
                        "id" to e.id,
                        "need" to e.need,
                        "remain" to e.remain
                    ))
                is BillingError.QuotaExceeded -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "error" to "QUOTA_EXCEEDED",
                        "id" to e.id,
                        "quota" to e.quota
                    ))
                is BillingError.InvalidSegmentRule -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "INVALID_SEGMENT_RULE", "reason" to e.reason))
                is BillingError.Technical -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(mapOf("error" to "TECHNICAL", "where" to e.where, "message" to (e.cause.message ?: "")))
            }
        }
    }

    @PostMapping("/campaigns")
    fun createCampaign(@RequestBody req: CreateCampaignRequest): ResponseEntity<Any> {
        val result = campaignService.create(CampaignService.CreateCommand(req.id, req.remain, req.dailyQuota))
        return when (result) {
            is Either.Right -> ResponseEntity.status(HttpStatus.CREATED).build()
            is Either.Left -> when (val e = result.value) {
                is BillingError.InvalidSegmentRule -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "INVALID_SEGMENT_RULE", "reason" to e.reason))
                else -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(mapOf("error" to "TECHNICAL", "where" to (if (e is BillingError.Technical) e.where else "create"), "message" to (if (e is BillingError.Technical) e.cause.message ?: "" else "")))
            }
        }
    }
}

// --- Wiring/Configuration ---

@Configuration
class Exception2Config(
    private val dao: Dao,
) {
    @Bean
    fun billingClient(): BillingClient = FakeBillingClient(0.0)

    @Bean
    fun billingGateway(billingClient: BillingClient): BillingGateway =
        RestBillingGateway(billingClient)

    @Bean
    fun campaignRepo(): CampaignRepo = JdbcCampaignRepo(dao)

    @Bean
    fun chargeService(repo: CampaignRepo, billing: BillingGateway): ChargeService =
        ChargeService(repo, billing)

    @Bean
    fun campaignService(repo: CampaignRepo): CampaignService = CampaignService(repo)
}
