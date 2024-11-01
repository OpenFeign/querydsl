package com.querydsl.example.ksp

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Person(
    @Id
    val id: Int,
    val name: String,
)
