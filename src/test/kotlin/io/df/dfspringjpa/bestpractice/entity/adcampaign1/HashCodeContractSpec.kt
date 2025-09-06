package io.df.dfspringjpa.bestpractice.entity.adcampaign1

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.util.*

class HashCodeContractSpec : StringSpec({
    fun period() = DateRange(LocalDate.now(), LocalDate.now().plusDays(7))
    fun krw(x: Long) = Money(x, "KRW")

    "equals가 true면 hashCode도 같다 (값 객체)" {
        val x = CampaignCode.of("cp-9")
        val y = CampaignCode.of("CP-9")
        (x == y) shouldBe true
        x.hashCode() shouldBe y.hashCode()
    }

    "equals가 true면 hashCode도 같다 (엔티티)" {
        val id = UUID.randomUUID()
        val a = AdCampaign.rehydrate(id, "A", CampaignCode.of("CP-1"), krw(10), period())
        val b = AdCampaign.rehydrate(id, "B", CampaignCode.of("CP-2"), krw(20), period())
        (a == b) shouldBe true
        a.hashCode() shouldBe b.hashCode()
    }

    "hashCode는 정보가 변하지 않는 한 일관적이어야 한다" {
        val x = CampaignCode.of("CP-STABLE")
        val h1 = x.hashCode()
        val h2 = x.hashCode()
        h1 shouldBe h2
    }

    // 주의: equals가 false라고 해서 hashCode가 달라야 하는 건 '아님'.
    // 단, 달라지면 해시테이블 성능이 좋아질 수 있다(권고).
})
