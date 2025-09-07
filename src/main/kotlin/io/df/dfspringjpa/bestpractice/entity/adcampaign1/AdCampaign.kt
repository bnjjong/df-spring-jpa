package io.df.dfspringjpa.bestpractice.entity.adcampaign1

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDate
import java.util.UUID

@Embeddable
data class Money(
    @Column(name = "amount", nullable = false)
    val amount: Long, // 최소 화폐단위(원)
    @Column(name = "currency", length = 3, nullable = false)
    val currency: String
) {
    init {
        require(amount >= 0)
        // 통화 코드는 3자리 대문자(ISO-4217 스타일, 예: KRW, USD). ENUM으로 바꾸는걸 추천
        require(currency.length == 3 && currency == currency.uppercase())
    }
    fun add(delta: Money): Money {
        // 서로 통화 코드가 같을 때만 합산 허용.
        require(currency == delta.currency)
        // data class의 불변성 유지. 새 인스턴스를 반환하고, 기존 Money는 변경되지 않음.
        return copy(amount = amount + delta.amount)
    }
}

@Embeddable
data class DateRange(
    @Column(name = "start_date", nullable = false) val start: LocalDate,
    @Column(name = "end_date", nullable = false) val end: LocalDate
) {
    init { require(!end.isBefore(start)) { "기간이 올바르지 않습니다." } }
}

@Embeddable
data class CampaignCode private constructor(
    @Column(name = "campaign_code", unique = true, nullable = false, length = 32)
    val value: String
) {
    init {
        require(value.isNotBlank())
        require(value.length <= 32)
    }
    companion object {
        fun of(raw: String) = CampaignCode(raw.trim().uppercase())
    }
}

/**
 * 임베디드 ID: UUID를 래핑하여 BINARY(16)으로 저장
 */
@Embeddable
data class AdCampaignId(
    @field:JdbcTypeCode(SqlTypes.BINARY)
    @field:Column(name = "id", columnDefinition = "BINARY(16)")
    val value: UUID = UUID.randomUUID()
)

@Entity
@Table(name = "ad_campaigns")
class AdCampaign internal constructor(
    name: String,
    code: CampaignCode,
    budget: Money,
    period: DateRange
) {
    // 내부 불변 식별자: 임베디드 ID 사용
    @EmbeddedId
    lateinit var id: AdCampaignId

    // 리하이드레이트용 보조 생성자 (식별자 주입)
    internal constructor(
        id: AdCampaignId,
        name: String,
        code: CampaignCode,
        budget: Money,
        period: DateRange
    ) : this(name, code, budget, period) {
        this.id = id
    }

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
        this.id = AdCampaignId()
        this.code = code
        this.budget = budget
        this.period = period
    }

    companion object {
        // 신규 생성 (ID 즉시 확정 → equals/hashCode 일관성 ↑)
        fun create(name: String, code: CampaignCode, budget: Money, period: DateRange): AdCampaign =
            AdCampaign(name, code, budget, period)

        // 재구성/리하이드레이트(다른 컨텍스트에서 같은 엔티티를 복원할 때)
        fun rehydrate(id: UUID, name: String, code: CampaignCode, budget: Money, period: DateRange): AdCampaign =
            AdCampaign(AdCampaignId(id), name, code, budget, period)
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

    /** equals/hashCode는 '동일 엔티티 여부'만 판단: id 기준 */
    override fun equals(other: Any?): Boolean {
        // ===는 같은 객체 인스턴스인지(메모리상 동일 참조인지)를 확인
        // ==는 값 동등성으로, 내부적으로 equals()를 호출
        if (this === other) return true
        if (other !is AdCampaign) return false
        // id는 생성 시 확정되므로 null 아닌 비교가 안전
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    // 호환성 편의: UUID 값에 직접 접근 필요할 때 사용
    val idValue: UUID get() = id.value
}
