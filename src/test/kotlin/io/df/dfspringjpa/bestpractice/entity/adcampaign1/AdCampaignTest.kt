package io.df.dfspringjpa.bestpractice.entity.adcampaign1

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate

class AdCampaignSpec : StringSpec({
    fun period() = DateRange(LocalDate.now(), LocalDate.now().plusDays(7))
    fun krw(x: Long) = Money(x, "KRW")

    "같은 id면 다른 속성이라도 동일 엔티티로 간주" {
        val a = AdCampaign("A", CampaignCode.of("CP-001"), krw(100_000), period())
        val b = AdCampaign("B", CampaignCode.of("CP-002"), krw(200_000), period())

        // 강제로 같은 id를 가정한 테스트(개념 설명 목적)
        val sameId = a.id
        val bWithSameId = b.apply {
            val idField = AdCampaign::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(this, sameId)
        }

        (a == bWithSameId) shouldBe true
        a.hashCode() shouldBe bWithSameId.hashCode()
    }

    "속성이 같아도 id가 다르면 다른 엔티티" {
        val p = period()
        val x = AdCampaign("X", CampaignCode.of("CP-100"), krw(50_000), p)
        val y = AdCampaign("X", CampaignCode.of("CP-100"), krw(50_000), p)
        (x == y) shouldBe false
    }

    "값 객체는 값으로 비교" {
        krw(10) shouldBe krw(10)
        period() shouldNotBe DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(8))
    }

    "도메인 로직: 예산 증액" {
        val c = AdCampaign("Z", CampaignCode.of("CP-200"), krw(0), period())
        c.increaseBudget(krw(30_000))
        c.budget.amount shouldBe 30_000
    }
})

