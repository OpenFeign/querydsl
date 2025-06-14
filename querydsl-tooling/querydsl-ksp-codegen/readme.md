# KSP for QueryDSL
Provides a lightweight and efficient way to generate `Q` classes for QueryDSL in Kotlin projects.  
Supports only jakarta annotations.

Let us know if you want support for other annotations.

## Setup

### Gradle
Add the KSP plugin and dependency for the processor in your `build.gradle.kts`

```kotlin
plugins {
    kotlin("jvm") version "2.1.21"
    id("com.google.devtools.ksp") version "2.1.21-2.0.2"
	kotlin("plugin.jpa")
	kotlin("plugin.serialization")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("jakarta.persistence:jakarta.persistence-api:${jpaVersion}")
	implementation("io.github.openfeign.querydsl:querydsl-jpa:${querydslVersion}")
	ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:${querydslVersion}")
}
```

And it may be necessary to [make your IDE aware of KSP generated code](https://kotlinlang.org/docs/ksp-quickstart.html#make-ide-aware-of-generated-code)

```kotlin
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}
```

### Settings (all optional)

| Name           |  Type                       | Default       | Description |
|:---------------|:----------------------------|:--------------|:------|
|enable          |Boolean                      |true           |Set to false will disable processing|
|indent          |String                       |" " (4 spaces)|Indent used in generated code files|
|prefix          |String                       |"Q"            |Prefix applied to generated classes|
|suffix          |String                       |""             |Suffix applied to generated classes|
|packageSuffix   |String                       |""             |Suffix applied to package name of generated classes|
|excludedPackages|String (comma separated list)|""             |List of packages that will be skipped in processing|
|excludedClasses |String (comma separated list)|""             |List of classes that will not be processed|
|includedPackages|String (comma separated list)|""             |List of packages included, empty means it will include everything|
|includedClasses |String (comma separated list)|""             |List of classes included, empty means it will include all|

Settings must be prefixed with 'querydsl.'

Add into your `build.gradle.kts` to configure.

```kotlin
// Example
ksp {
    arg("querydsl.prefix", "QQ")
    arg("querydsl.excludedPackages", "com.example, com.sample")
}
```
