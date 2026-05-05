# KSP for QueryDSL
Provides a lightweight and efficient way to generate `Q` classes for QueryDSL in Kotlin projects.  
Supports only jakarta annotations.

Let us know if you want support for other annotations.

Both **pure-Kotlin** entities and **Java entities consumed from Kotlin** are
supported (see "Mixed Java/Kotlin sources" below). The processor runs under
KSP2, which is the default runtime for the KSP Gradle plugin since the 2.x line.

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

## Mixed Java/Kotlin sources

A common reason to use this processor instead of `querydsl-apt` is when the
**entities are Java** but the queries are written in **Kotlin**. With Gradle's
default Java/Kotlin compile order, `querydsl-apt`'s Java Q-classes aren't on
the classpath when Kotlin sources compile, so Kotlin code can't reference them.
KSP runs as part of `kspKotlin`, sees Java sources alongside Kotlin sources,
and emits `.kt` Q-classes that Kotlin can use immediately.

No extra plugin args are required — drop your `@Entity` Java classes under
`src/main/java` and they'll be picked up:

```java
// src/main/java/com/example/Person.java
@Entity
public class Person {
    @Id private Long id;
    private String name;
    @Embedded private Address address;
    @Transient private String cachedDisplay; // skipped
    public static final String CONSTANT = "x"; // skipped
    // getters/setters...
}
```

```kotlin
// src/main/kotlin/com/example/Repository.kt
val q = QPerson.person
JPAQueryFactory(em).selectFrom(q).where(q.address.city.eq("London")).fetch()
```

A complete runnable Gradle project lives in
[querydsl-examples/querydsl-example-ksp-codegen](../../querydsl-examples/querydsl-example-ksp-codegen).
It carries Kotlin entities under `src/main/kotlin` and a Java entity (`Branch`,
self-referencing) under `src/main/java`, both queried side-by-side from Kotlin
tests — the same shape a typical mixed Java/Kotlin codebase has.

### Limitations

- JPA `@Access(PROPERTY)` (annotations on getters) is **not yet supported** —
  put your JPA annotations on fields. This matches `querydsl-apt`'s default.
- Maven KSP integration is out of scope; `kotlin-maven-plugin` does not natively
  run KSP processors. Use Gradle for KSP, or fall back to KAPT +
  `querydsl-kotlin-codegen` on Maven.
