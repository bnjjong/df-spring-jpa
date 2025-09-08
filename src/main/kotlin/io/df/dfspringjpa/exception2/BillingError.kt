package io.df.dfspringjpa.exception2

//“기술적” 실패(네트워크/DB 등)는 Technical 등 한 타입으로 흡수하고 cause를 보존.
//“업무적” 실패는 NotEnoughBudget처럼 명확한 필드/메시지를 타이핑.
sealed interface BillingError {
    data class NotFound(val id: String) : BillingError
    data class NotEnoughBudget(val id: String, val need: Long, val remain: Long) : BillingError
    data class QuotaExceeded(val id: String, val quota: Long) : BillingError
    data class InvalidSegmentRule(val reason: String) : BillingError
    data class Technical(val where: String, val cause: Throwable) : BillingError
}

// 성공 결과(예: 영수증)
data class Receipt(val campaignId: String, val amount: Long, val txId: String)
