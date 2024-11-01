plugins {
	kotlin("jvm") version "2.0.21"
	id("com.google.devtools.ksp") version "2.0.21-1.0.25"
	kotlin("plugin.jpa") version "2.0.21"
	kotlin("plugin.serialization") version "2.0.21"
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
	implementation("io.github.openfeign.querydsl:querydsl-core:6.9-SNAPSHOT")
	ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:6.9-SNAPSHOT")

	testImplementation("io.github.openfeign.querydsl:querydsl-jpa:6.9-SNAPSHOT")
	testImplementation("org.assertj:assertj-core:3.26.3")
	testImplementation("org.hibernate.orm:hibernate-core:6.6.0.Final")
	testImplementation("com.h2database:h2:2.2.224")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.0.21")
}
