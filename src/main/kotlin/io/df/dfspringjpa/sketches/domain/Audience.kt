package io.df.dfspringjpa.sketches.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 오디언스 엔티티
 * 200만명 사용자 데이터를 위한 JPA 엔티티
 */
@Entity
@Table(
    name = "audience",
    indexes = [
        Index(name = "idx_gender_age", columnList = "gender, age"),
        Index(name = "idx_address", columnList = "address"),
        Index(name = "idx_monthly_sales", columnList = "monthly_sales"),
        Index(name = "idx_segment_condition", columnList = "gender, age, address, monthly_sales")
    ]
)
data class Audience(

    /**
     * 성별 (남자/여자)
     */
    @Column(name = "gender", nullable = false, length = 10)
    val gender: String,

    /**
     * 주소 (시/도)
     */
    @Column(name = "address", nullable = false, length = 50)
    val address: String,

    /**
     * 나이
     */
    @Column(name = "age", nullable = false)
    val age: Int,

    /**
     * 월 매출액
     */
    @Column(name = "monthly_sales", nullable = false, precision = 15, scale = 2)
    val monthlySales: BigDecimal,

    /**
     * 홈텍스 연봉 여부
     */
    @Column(name = "is_hometax_salary", nullable = false)
    val isHometaxSalary: Boolean,

    /**
     * 멤버십 등급 (BRONZE/SILVER/GOLD/PLATINUM/DIAMOND)
     */
    @Column(name = "membership_level", nullable = false, length = 20)
    val membershipLevel: String,

    /**
     * 업종 카테고리
     */
    @Column(name = "industry_category", nullable = false, length = 50)
    val industryCategory: String,


) {

    @EmbeddedId
    var id: AudienceId = AudienceId()

    /**
     * JPA용 기본 생성자
     */
    constructor() : this(
        gender = "",
        address = "",
        age = 0,
        monthlySales = BigDecimal.ZERO,
        isHometaxSalary = false,
        membershipLevel = "",
        industryCategory = ""
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Audience
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
