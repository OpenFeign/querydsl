package com.querydsl.example.ksp

import com.querydsl.core.annotations.QueryProjection

data class PersonClassConstructorDTO @QueryProjection constructor(
    val id: Int,
    val name: String,
)

@QueryProjection
data class PersonClassDTO (val id: Int, val name: String)
