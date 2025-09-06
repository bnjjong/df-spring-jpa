package io.df.dfspringjpa.bestpractice.entity.adcampaign1

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

// ⚠️ 문제: data class로 equals/hashCode가 모든 프로퍼티 기반.
// 이름/기간이 같으면 서로 다른 캠페인도 "같다"고 오판.
// 해시 컬렉션(Set)에 넣고 값 변경 시 탐색/삭제 실패 가능.
@Entity
@Table(name = "ad_campaigns_as_is")
data class AdCampaignAsIs(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    var id: java.util.UUID? = null, // null로 두고 JPA 생성(=동등성 혼란)
    var name: String,
    var code: String,               // 외부 코드지만 불변 보장 X
    var budgetAmount: Long,
    var budgetCurrency: String,
    var startDate: java.time.LocalDate,
    var endDate: java.time.LocalDate
)
