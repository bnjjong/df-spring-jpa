package io.df.dfspringjpa.exception2

import arrow.core.Either
import arrow.core.flatMap

interface BillingGateway {
    fun issueReceipt(id: String, amount: Long): Either<BillingError, Receipt>
}

class RestBillingGateway(private val http: BillingClient) : BillingGateway {
    override fun issueReceipt(id: String, amount: Long): Either<BillingError, Receipt> =
        Either.catch { http.postIssue(id, amount) }
            .mapLeft { th -> BillingError.Technical("billing.issue", th) }
}
