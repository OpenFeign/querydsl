package com.querydsl.example.ksp

import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Cat(
    @Id
    val id: Int,
    val name: String,
    val isTailUpright: Boolean,
    val fluffiness: String? = null,

    @ManyToOne
    val owner: Person? = null,

    @Enumerated(EnumType.STRING)
    val catType: CatType? = CatType.HOUSE,
)
