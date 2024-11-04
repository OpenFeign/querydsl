val kotlinVersion = findProperty("kotlin.version") as String
val jpaVersion = findProperty("jpa.version") as String
val hibernateVersion = findProperty("hibernate.version") as String
val h2Version = findProperty("h2.version") as String
val querydslVersion = findProperty("querydsl.version") as String
val assertjVersion = findProperty("assertj.version") as String

plugins {
	kotlin("jvm")
	id("com.google.devtools.ksp")
	kotlin("plugin.jpa")
	kotlin("plugin.serialization")
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation("jakarta.persistence:jakarta.persistence-api:${jpaVersion}")
	implementation("io.github.openfeign.querydsl:querydsl-core:${querydslVersion}")
	ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:${querydslVersion}")

	testImplementation("io.github.openfeign.querydsl:querydsl-jpa:${querydslVersion}")
	testImplementation("org.assertj:assertj-core:${assertjVersion}")
	testImplementation("org.hibernate.orm:hibernate-core:${hibernateVersion}")
	testImplementation("com.h2database:h2:${h2Version}")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${kotlinVersion}")
}
