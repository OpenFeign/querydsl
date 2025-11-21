package com.querydsl.example.ksp

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.YearMonth
import java.util.UUID

@Entity
class Invoice(
    @Id
    val id: UUID,

    @Column(nullable = false, length = 7)
    @Convert(converter = YearMonthConverter::class)
    val month: YearMonth?,

    @Column(nullable = false, precision = 30, scale = 10)
    @Convert(converter = MoneyConverter::class)
    val amount: Money?,
)

