package com.querydsl.example.ksp

import com.querydsl.core.annotations.QueryProjection

@QueryProjection
data class PersonDTO (
    val id: Int,
    val name: String,
)
