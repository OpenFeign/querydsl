---
layout: default
title: OpenRewrite Migration
parent: Guides
nav_order: 5
---

# OpenRewrite Migration

{: .fs-9 }

Migrate a Querydsl project to fluentQ automatically with [OpenRewrite](https://docs.openrewrite.org/).
{: .fs-6 .fw-300 }

---

fluentQ publishes an OpenRewrite recipe artifact, **`{{ site.group_id }}:fluentq-rewrite`**, that
rewrites a downstream project from Querydsl to fluentQ — no hand-editing of imports or coordinates.

## What it does

The recipe `io.github.openfeign.fluentq.rewrite.MigrateQuerydslToFluentQ`:

- Renames Java/Kotlin/Scala packages: `com.querydsl.*` and `io.github.openfeign.querydsl.*` → `fluentq.*`
  (imports, fully-qualified names, `package` declarations).
- Rewrites Maven coordinates: group id → `{{ site.group_id }}`, artifacts `querydsl-*` → `fluentq-*`,
  version → `{{ site.querydsl_version }}` (dependencies, `dependencyManagement`, and the BOM
  `querydsl-bom` → `fluentq-bom`).
- Rewrites the build plugin `querydsl-maven-plugin` → `fluentq-maven-plugin`.

It works for both the original Querydsl (`com.querydsl`) and the OpenFeign fork
(`io.github.openfeign.querydsl`).

## Maven

Add the recipe artifact to the `rewrite-maven-plugin` and activate the recipe:

```xml
<plugin>
  <groupId>org.openrewrite.maven</groupId>
  <artifactId>rewrite-maven-plugin</artifactId>
  <version>6.42.0</version>
  <configuration>
    <activeRecipes>
      <recipe>io.github.openfeign.fluentq.rewrite.MigrateQuerydslToFluentQ</recipe>
    </activeRecipes>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>{{ site.group_id }}</groupId>
      <artifactId>fluentq-rewrite</artifactId>
      <version>{{ site.querydsl_version }}</version>
    </dependency>
  </dependencies>
</plugin>
```

```bash
./mvnw rewrite:run
```

Or run it once without editing your `pom.xml`:

```bash
./mvnw -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates={{ site.group_id }}:fluentq-rewrite:{{ site.querydsl_version }} \
  -Drewrite.activeRecipes=io.github.openfeign.fluentq.rewrite.MigrateQuerydslToFluentQ
```

Use `rewrite:dryRun` instead of `rewrite:run` to preview the diff (written to
`target/rewrite/rewrite.patch`) before applying it.

## Gradle

```groovy
plugins {
  id("org.openrewrite.rewrite") version("latest.release")
}

rewrite {
  activeRecipe("io.github.openfeign.fluentq.rewrite.MigrateQuerydslToFluentQ")
}

dependencies {
  rewrite("{{ site.group_id }}:fluentq-rewrite:{{ site.querydsl_version }}")
}
```

```bash
./gradlew rewriteRun   # or rewriteDryRun to preview
```

## Limitations & manual steps

- **Annotation processor class in `pom.xml`** — if you configured a processor class by hand the old
  way (`<processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>`), update the package to
  `fluentq.apt.*` manually. The recommended `maven-compiler-plugin` + `annotationProcessorPaths`
  setup discovers the processor from the `fluentq-apt` artifact and needs no class change.
- **Dropped modules** — `querydsl-jdo`, `querydsl-lucene3/4/5`, and `querydsl-hibernate-search` have
  no fluentQ equivalent and are intentionally left untouched. See the
  [Migration Guide](../migration.md) for alternatives.
- **Review the result** — always review the diff and run a full build (`mvn clean install`) after the
  recipe; regenerate Q-types and confirm your queries compile.

## Roadmap

The `fluentq-rewrite` artifact is the home for fluentQ migration recipes. Planned additions:

- `MigrateMysemaQuerydslToFluentQ` — ancient Querydsl 3 (`com.mysema`) with its older syntax.
- `MigrateJooqToFluentQ` — migrate from jOOQ.
- `MigrateHibernateCriteriaToFluentQ` — migrate from the Hibernate Criteria API.

Each lives in the same artifact under `META-INF/rewrite/`, so updating the `fluentq-rewrite`
dependency makes new recipes available.
