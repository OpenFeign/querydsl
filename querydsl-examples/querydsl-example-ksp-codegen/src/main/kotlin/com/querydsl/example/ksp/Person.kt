package com.querydsl.example.ksp

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Person(
    @Id
    val id: Int,
    val name: String,

    @OneToMany(mappedBy = "owner")
    val cats: List<Cat>? = null,
)
