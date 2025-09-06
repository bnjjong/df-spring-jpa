package io.df.dfspringjpa.bestpractice.entity.adcampaign1

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDate
import java.util.UUID

@Embeddable
data class Money(
    @Column(name = "amount", nullable = false) val amount: Long, // 최소 화폐단위(원)
    @Column(name = "currency", length = 3, nullable = false) val currency: String
)

@Embeddable
data class DateRange(
    @Column(name = "start_date", nullable = false) val start: LocalDate,
    @Column(name = "end_date", nullable = false) val end: LocalDate
) {
    init { require(!end.isBefore(start)) { "기간이 올바르지 않습니다." } }
}

@Embeddable
data class CampaignCode(
    @Column(name = "campaign_code", unique = true, nullable = false, length = 32)
    val value: String
)

@Entity
@Table(name = "ad_campaigns")
class AdCampaign internal constructor(
    name: String,
    code: CampaignCode,
    budget: Money,
    period: DateRange
) {
    // 내부 불변 식별자: 생성 시 확정(=equals/hashCode 안전)
    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

    @Column(nullable = false)
    var name: String = name
        protected set

    @Embedded
    lateinit var code: CampaignCode
        protected set

    @Embedded
    lateinit var budget: Money
        protected set

    @Embedded
    lateinit var period: DateRange
        protected set

    init {
        this.code = code
        this.budget = budget
        this.period = period
    }

    /** 비즈니스 로직 예시: 예산 증액(음수 금지) */
    fun increaseBudget(delta: Money) {
        require(delta.currency == budget.currency) { "통화 불일치" }
        require(delta.amount >= 0) { "증액은 0 이상" }
        this.budget = budget.copy(amount = budget.amount + delta.amount)
    }

    /** 캠페인명 변경(식별성과 무관) */
    fun rename(newName: String) {
        require(newName.isNotBlank())
        this.name = newName.trim()
    }

    /** ⚠ equals/hashCode는 '동일 엔티티 여부'만 판단: id 기준 */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AdCampaign) return false
        // id는 생성 시 확정되므로 null 아닌 비교가 안전
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
