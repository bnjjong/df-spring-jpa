package io.df.dfspringjpa.bestpractice.entity.adcampaign1

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class EqualsContractSpec : StringSpec({
    fun period() = DateRange(LocalDate.now(), LocalDate.now().plusDays(7))
    fun krw(x: Long) = Money(x, "KRW")

    "값 객체(CampaignCode)의 equals 계약: 반사/대칭/추이/일관/Null" {
        val x = CampaignCode.of(" cp-001 ")
        val y = CampaignCode.of("CP-001")
        val z = CampaignCode.of("cp-001")

        // 반사성
        (x == x) shouldBe true

        // 대칭성
        (x == y) shouldBe true
        (y == x) shouldBe true

        // 추이성
        (x == y && y == z && x == z) shouldBe true

        // 일관성 (여러번 호출해도 동일)
        repeat(5) { (x == y) shouldBe true }

        // Null 비교
        (x == null) shouldBe false
    }

    "엔티티(AdCampaign)의 equals는 ID 기준" {
        val id = java.util.UUID.randomUUID()
        val a = AdCampaign.rehydrate(id, "A", CampaignCode.of("CP-1"), krw(10), period())
        val b = AdCampaign.rehydrate(id, "B", CampaignCode.of("CP-2"), krw(20), period())

        // 속성은 달라도 같은 ID면 동등
        (a == b) shouldBe true

        // 반사/대칭/추이/일관/Null
        (a == a) shouldBe true
        (a == b && b == a) shouldBe true
        val c = AdCampaign.rehydrate(id, "C", CampaignCode.of("CP-3"), krw(30), period())
        (a == b && b == c && a == c) shouldBe true
        repeat(3) { (a == b) shouldBe true }
        (a == null) shouldBe false
    }
})
