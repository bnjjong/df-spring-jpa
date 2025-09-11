package io.df.dfspringjpa.sketches.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val gender: String,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val age: Int,

    @Column(name = "monthly_sales", nullable = false, precision = 19, scale = 2)
    val monthlySales: BigDecimal
)
