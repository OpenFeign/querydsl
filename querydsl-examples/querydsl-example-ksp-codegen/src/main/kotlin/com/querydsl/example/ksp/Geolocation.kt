package com.querydsl.example.ksp

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.locationtech.jts.geom.Point

@Entity
class Geolocation(
	@Id
	val id: Int,
	@Column(columnDefinition = "geography(Point, 4326)")
	var location: Point? = null,
)
