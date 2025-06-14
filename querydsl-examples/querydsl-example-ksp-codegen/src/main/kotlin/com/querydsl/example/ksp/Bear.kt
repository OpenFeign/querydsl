package com.querydsl.example.ksp

import com.querydsl.core.annotations.QueryProjection
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Bear(
	@Id
	val id: Int,
	val name: String,
	val location: String
)

class BearSimplifiedProjection @QueryProjection constructor(
	val id: Int,
	val name: String,
) {
	var age: Int = 0
}
