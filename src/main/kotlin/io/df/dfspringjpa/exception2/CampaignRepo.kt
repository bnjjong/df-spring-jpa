package io.df.dfspringjpa.exception2

import arrow.core.Either

interface CampaignRepo {
    fun find(id: String): Either<BillingError, Campaign>
    fun updateRemain(id: String, newRemain: Long): Either<BillingError, Unit>
}

// 최소 구현을 위한 예외 스텁 정의
class NotFoundException(message: String = "not found") : RuntimeException(message)

class JdbcCampaignRepo(private val dao: Dao) : CampaignRepo {

    override fun find(id: String): Either<BillingError, Campaign> =
        Either.catch { dao.selectCampaign(id) }
            .mapLeft { th ->
                if (th is NotFoundException)
                    BillingError.NotFound(id)
                else
                    BillingError.Technical(
                    "repo.find",
                    th
                )
            }

    override fun updateRemain(id: String, newRemain: Long): Either<BillingError, Unit> =
        Either.catch { dao.updateRemain(id, newRemain) }
            .mapLeft { th -> BillingError.Technical("repo.updateRemain", th) }
}