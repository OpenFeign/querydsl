package com.querydsl.example.ksp

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
class Dog(
    @Id
    val id: Int,
    val name: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    val tag: Tag
)
