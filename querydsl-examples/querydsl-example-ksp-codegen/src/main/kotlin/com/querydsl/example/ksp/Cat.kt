package com.querydsl.example.ksp

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Cat(
    @Id
    val id: Int,
    val name: String,
    val isTailUpright: Boolean
)
