package io.df.dfspringjpa.exception2


// 외부 청구 시스템 클라이언트 인터페이스
interface BillingClient {
    /**
     * 캠페인 ID와 금액을 전달하여 영수증을 발급한다.
     * 실패 시 BillingClientException 하위 예외를 던질 수 있다.
     */
    @Throws(BillingClientException::class)
    fun postIssue(campaignId: String, amount: Long): Receipt
}

// 클라이언트 전용 예외 (도메인 레벨에서는 어댑터에서 Technical 등으로 매핑)
open class BillingClientException(message: String, cause: Throwable? = null)
    : RuntimeException(message, cause)

class BillingClientBadRequest(message: String, cause: Throwable? = null)
    : BillingClientException(message, cause)

class BillingClientUnavailable(message: String, cause: Throwable? = null)
    : BillingClientException(message, cause)

/**
 * 테스트/로컬 개발용 인메모리 클라이언트
 * - 항상 성공하거나, 설정에 따라 간헐적 실패를 시뮬레이션
 */
class FakeBillingClient(
    private val failRate: Double = 0.0 // 0.0 ~ 1.0 (실패 확률)
) : BillingClient {

    private val rnd = java.util.Random()

    override fun postIssue(campaignId: String, amount: Long): Receipt {
        if (rnd.nextDouble() < failRate) {
            throw BillingClientUnavailable("temporary outage (simulated)")
        }
        return Receipt(
            campaignId = campaignId,
            amount = amount,
            txId = "TX-" + java.util.UUID.randomUUID().toString().replace("-", "").take(16)
        )
    }
}
