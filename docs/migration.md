---
layout: default
title: Migration Guide
nav_order: 6
---

# Migration Guide

The original Querydsl project (`com.querydsl`, hosted at querydsl.com) has
been inactive since 2020. The last release, 5.0.0, was published in July 2021.
The website querydsl.com and its documentation may go offline at any time.

This fork, maintained under the [OpenFeign](https://github.com/OpenFeign)
organization, provides regular releases, community patches, Jakarta EE
support, and new modules like R2DBC and Kotlin. This guide covers everything
you need to migrate.

## Why Migrate?

- **Active maintenance** — regular releases, dependency updates, security
  patches
- **Jakarta EE support** — works with Hibernate 6+, Spring Boot 3+, and
  Jakarta-namespace APIs
- **Java 17+** — takes advantage of modern JDK features
- **New modules** — R2DBC for reactive database access, Kotlin extensions for
  idiomatic syntax
- **Community-driven** — PRs are reviewed and merged, issues get attention

## What Stays the Same

- **Java package names** — all classes remain in `com.querydsl.*` packages. No
  source code changes are needed for import statements.
- **Artifact IDs** — `querydsl-jpa`, `querydsl-sql`, `querydsl-mongodb`, etc.
  are unchanged.
- **API surface** — the Querydsl fluent API is the same. Your existing query
  code will compile and run without changes (after the groupId and namespace
  migration below).

## What Changes

### 1. Maven GroupId

Replace all occurrences of `com.querydsl` with `{{ site.group_id }}`:

**Before:**

```xml
<dependency>
  <groupId>com.querydsl</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>5.0.0</version>
</dependency>
```

**After:**

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

A quick way to do this across your project:

```bash
find . -name "pom.xml" -exec sed -i 's|<groupId>com.querydsl</groupId>|<groupId>{{ site.group_id }}</groupId>|g' {} +
```

### 2. Jakarta EE (javax → jakarta)

Starting with version 6.0, this fork requires **Jakarta EE** (the
`jakarta.*` namespace) instead of Java EE (`javax.*`).

| Before | After |
|:-------|:------|
| `javax.persistence.Entity` | `jakarta.persistence.Entity` |
| `javax.persistence.EntityManager` | `jakarta.persistence.EntityManager` |
| `javax.inject.Inject` | `jakarta.inject.Inject` |
| `javax.annotation.Generated` | `jakarta.annotation.Generated` |

You also need Jakarta-compatible versions of your JPA provider:

| Provider | Minimum Version |
|:---------|:----------------|
| Hibernate | 6.0+ |
| EclipseLink | 4.0+ |
| Spring Boot | 3.0+ |

### 3. Java Version

This fork requires **Java 17** or later. If you are on Java 8 or 11, you need
to upgrade your JDK before migrating.

### 4. Annotation Processor Configuration

The old `com.mysema.maven:apt-maven-plugin` is no longer recommended. Use
`maven-compiler-plugin` with annotation processor dependencies instead:

**Before (apt-maven-plugin):**

```xml
<plugin>
  <groupId>com.mysema.maven</groupId>
  <artifactId>apt-maven-plugin</artifactId>
  <version>1.1.3</version>
  <executions>
    <execution>
      <goals>
        <goal>process</goal>
      </goals>
      <configuration>
        <outputDirectory>target/generated-sources/java</outputDirectory>
        <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
      </configuration>
    </execution>
  </executions>
</plugin>
```

**After (maven-compiler-plugin):**

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <generatedSourcesDirectory>target/generated-sources/java</generatedSourcesDirectory>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>{{ site.group_id }}</groupId>
      <artifactId>querydsl-apt</artifactId>
      <version>{{ site.querydsl_version }}</version>
      <classifier>jpa</classifier>
    </dependency>
    <dependency>
      <groupId>jakarta.persistence</groupId>
      <artifactId>jakarta.persistence-api</artifactId>
      <version>3.1.0</version>
    </dependency>
  </dependencies>
</plugin>
```

Available classifiers for `querydsl-apt`:

| Classifier | Annotation Processor |
|:-----------|:--------------------|
| `jpa` | `JPAAnnotationProcessor` |
| `hibernate` | `HibernateAnnotationProcessor` |
| `general` | `QuerydslAnnotationProcessor` |

### 5. Removed Modules

The following modules have been removed from this fork:

| Module | Reason | Alternative |
|:-------|:-------|:------------|
| `querydsl-jdo` | JDO usage has declined significantly | Use JPA instead |
| `querydsl-lucene3` | Lucene 3 is EOL | Use `querydsl-lucene9` or `querydsl-lucene10` |
| `querydsl-lucene4` | Lucene 4 is EOL | Use `querydsl-lucene9` or `querydsl-lucene10` |
| `querydsl-lucene5` | Lucene 5 is EOL | Use `querydsl-lucene9` or `querydsl-lucene10` |
| `querydsl-hibernate-search` | Hibernate Search has its own query DSL | Use Hibernate Search API directly |

If you depend on any of these modules, you have two options:
1. Keep using the original `com.querydsl:5.0.0` artifacts for those specific
   modules alongside the new fork for everything else (the Java packages are
   the same, so be careful with classpath conflicts).
2. Migrate to the native APIs of those backends.

### 6. New Modules

| Module | Description |
|:-------|:------------|
| [`querydsl-r2dbc`]({{ site.baseurl }}/tutorials/r2dbc) | Reactive, non-blocking database access via R2DBC and Project Reactor |
| [`querydsl-lucene9`]({{ site.baseurl }}/tutorials/lucene) | Lucene 9 integration (Java 17+), replacing the old lucene3/4/5 modules |
| [`querydsl-lucene10`]({{ site.baseurl }}/tutorials/lucene) | Lucene 10 integration (Java 21+) |
| [`querydsl-kotlin`]({{ site.baseurl }}/tutorials/kotlin) | Kotlin extension functions — use `+`, `-`, `*`, `/`, `%` operators on expressions |

## Step-by-Step Migration

1. **Upgrade your JDK** to 17 or later.
2. **Migrate to Jakarta EE** if not already done — replace `javax.persistence`
   with `jakarta.persistence`, update your JPA provider.
3. **Update your POM** — change the groupId from `com.querydsl` to
   `{{ site.group_id }}` for all Querydsl dependencies.
4. **Update the version** — set the version to `{{ site.querydsl_version }}`.
5. **Replace apt-maven-plugin** — switch to `maven-compiler-plugin` with
   `querydsl-apt` as a compiler dependency.
6. **Remove references to dropped modules** — if you used `querydsl-jdo`,
   `querydsl-lucene*`, or `querydsl-hibernate-search`, find alternatives.
7. **Clean and rebuild** — run `mvn clean install` to regenerate all Q-types.
8. **Run your tests** — verify that all queries work as expected.

## Gradle Users

For Gradle, the same groupId change applies:

**Before:**

```groovy
implementation 'com.querydsl:querydsl-jpa:5.0.0'
annotationProcessor 'com.querydsl:querydsl-apt:5.0.0:jpa'
```

**After:**

```groovy
implementation '{{ site.group_id }}:querydsl-jpa:{{ site.querydsl_version }}'
annotationProcessor '{{ site.group_id }}:querydsl-apt:{{ site.querydsl_version }}:jpa'
```

## Spring Boot Integration

If you use Spring Boot 3+, the migration is straightforward since Spring Boot
3 already requires Jakarta EE and Java 17:

```xml
<properties>
  <querydsl.version>{{ site.querydsl_version }}</querydsl.version>
</properties>

<dependencies>
  <dependency>
    <groupId>{{ site.group_id }}</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>${querydsl.version}</version>
  </dependency>
</dependencies>
```

For Spring Boot 2.x users: you need to migrate to Spring Boot 3+ first (which
also requires Jakarta EE and Java 17), and then migrate Querydsl.

## Getting Help

If you encounter issues during migration, open a discussion on the
[GitHub repository](https://github.com/OpenFeign/querydsl/discussions).
