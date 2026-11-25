---
layout: default
title: Migration to fluentQ (8.x)
nav_order: 7
---

# Migration to fluentQ (8.x)
{: .fs-9 }

This guide details the major rename of the project from Querydsl to **fluentQ** starting with the 8.0 release, and how to migrate your projects.
{: .fs-6 .fw-300 }

---

## Overview

In the 8.x release, the project has been fully renamed from **Querydsl** to **fluentQ**. This change introduces new Maven coordinates and a simplified Java package namespace:

* **Group ID**: `io.github.openfeign.fluentq`
* **Artifact IDs**: `fluentq-core`, `fluentq-jpa`, `fluentq-sql`, etc.
* **Java Package Prefix**: `fluentq` (e.g. `fluentq.core.*`, `fluentq.jpa.*`)

To ease the transition, **full backwards compatibility is maintained** via deprecated wrapper modules and classes.

---

## Gradual Migration (Using Backwards Compatible Modules)

If you want to upgrade to 8.x but are not ready to update your Java imports and codebase package references yet, you can use the **backwards-compatibility modules**. 

These modules publish artifacts under the legacy `io.github.openfeign.querydsl` Group ID, but delegate execution to the new `fluentq` codebase:

### Maven Coordinates for Backwards Compatibility
```xml
<dependency>
  <groupId>io.github.openfeign.querydsl</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>8.0</version>
</dependency>
```

> [!NOTE]
> All classes in `com.querydsl.*` and `io.github.openfeign.querydsl.*` inside these compatibility artifacts are annotated with `@Deprecated`. Under the hood, they extend/delegate to the corresponding `fluentq.*` classes. This ensures your project compiles and runs exactly as before, while signaling the deprecation in your IDE.

---

## Full Migration (Moving to fluentQ)

To fully migrate to the new `fluentq` packages and dependencies, you need to update your build files and import statements.

### 1. Update Maven Coordinates
Update your `pom.xml` dependencies to point to the new GAV:

**Before:**
```xml
<dependency>
  <groupId>io.github.openfeign.querydsl</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>7.3</version>
</dependency>
```

**After:**
```xml
<dependency>
  <groupId>io.github.openfeign.fluentq</groupId>
  <artifactId>fluentq-jpa</artifactId>
  <version>8.0</version>
</dependency>
```

### 2. Update Package Imports
Replace imports of legacy packages with the new `fluentq` package structure:

* Replace `com.querydsl` with `fluentq`
* Replace `io.github.openfeign.querydsl` with `fluentq`

---

## Breaking Changes

These apply to the `fluentq` artifacts and to the backwards-compatibility artifacts alike.

### `FactoryExpressionTransformer` (JPA)

Hibernate ORM 8 removed `org.hibernate.transform.ResultTransformer`, so
`FactoryExpressionTransformer` now implements `org.hibernate.query.TupleTransformer` instead:

* It no longer implements `ResultTransformer`, `ResultListTransformer` or `Serializable`.
* `transformList` is removed. It returned its argument unchanged.

Code that passes a `FactoryExpressionTransformer` to `Query.setResultTransformer(...)` must
call `Query.setTupleTransformer(...)` instead. Queries built with fluentQ need no change.

### Jakarta Persistence 4 and Hibernate ORM 8

8.x requires Java 21. `fluentq-jpa` requires Jakarta Persistence 4, with Hibernate ORM 8 or
EclipseLink 6. Stay on 7.x for Jakarta Persistence 3.2 and Hibernate ORM 7. See
[Supported Versions]({{ site.baseurl }}/tutorials/jpa#supported-versions).

### Hibernate queries and clauses

* `HibernateQuery.createQuery()` returns `org.hibernate.query.SelectionQuery` instead of
  `org.hibernate.query.Query`.
* `SessionHolder.createQuery(String)` returns `org.hibernate.query.MutationOrSelectionQuery`.
  Update custom `SessionHolder` implementations.
* `setLockMode` is removed from `HibernateDeleteClause`, `HibernateInsertClause` and
  `HibernateUpdateClause`. It never had an effect on those statements.
* Hibernate ORM 8 has one lock mode per query. `setLockMode` on a query throws
  `UnsupportedOperationException` when it is called with a different mode than an earlier call.
* The protected `lockModes` map on `AbstractHibernateQuery` is replaced by a single `lockMode`
  field.

---

## Automated Migration with OpenRewrite

For a deterministic, repeatable migration, use the published OpenRewrite recipe
`io.github.openfeign.fluentq.rewrite.MigrateQuerydslToFluentQ` (artifact
`{{ site.group_id }}:fluentq-rewrite`). It rewrites the Maven coordinates, the Java/Kotlin/Scala
package imports, and the build plugin in one pass, for both Maven and Gradle projects.

See the [OpenRewrite Migration guide]({{ site.baseurl }}/guides/openrewrite-recipe) for the full
Maven and Gradle setup, the Querydsl 5 variant, and limitations.

---

## Automated Migration with AI / LLM

Since this migration is a mechanical rename of dependencies and package prefixes, you can easily use an AI coding assistant (like Gemini or Claude) to automate the process for you.

### Copy-Pasteable Migration Prompt

Use the following prompt in your AI assistant to migrate your repository:

````markdown
I want to migrate this project from Querydsl to fluentQ (version 8.0). Please scan and refactor the codebase with the following rules:

1. **Maven Dependencies (pom.xml)**:
   - Change parent/dependency Group ID from `io.github.openfeign.querydsl` to `io.github.openfeign.fluentq`.
   - Rename dependency/plugin Artifact IDs starting with `querydsl-` to `fluentq-` (e.g. `querydsl-jpa` to `fluentq-jpa`, `querydsl-apt` to `fluentq-apt`).
   - Set the version of all these dependencies/plugins to `8.0` (or `8.0-SNAPSHOT`).

2. **Java / Kotlin / Scala Source Files**:
   - In package declarations and imports, replace all occurrences of `com.querydsl` with `fluentq`.
   - In package declarations and imports, replace all occurrences of `io.github.openfeign.querydsl` with `fluentq`.
   - Replace any references in comments/Javadocs or string literals where Querydsl classes are instantiated dynamically.
   - For any generated Q-classes, make sure to update annotation processor configurations to use the new `fluentq.apt.FluentQAnnotationProcessor`.

3. **Verify**:
   - Verify that the project compiles cleanly after these replacements.
````
