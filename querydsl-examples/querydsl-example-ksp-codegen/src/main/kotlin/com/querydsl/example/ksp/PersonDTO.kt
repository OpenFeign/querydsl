package com.querydsl.example.ksp

import com.querydsl.core.annotations.QueryProjection

data class PersonClassConstructorDto @QueryProjection constructor(
    val id: Int,
    val name: String,
)

@QueryProjection
data class PersonClassDto (val id: Int, val name: String)
