# 8.x Branch Overview (fluentQ Transition)

## Divergence point

`52a3965fb` — CI: trigger build, deploy and codeql workflows on 8.x branch

Master is ~404 commits ahead of this point. The 8.x branch has ~103 merge conflicts with master due to the fluentQ restructure below.

## What was introduced on 8.x (not in master)

### 1. FluentQ package migration
- All `com.querydsl.*` packages renamed to `fluentq.*` under two parallel directory trees:
  - `querydsl/` — legacy paths (`querydsl-libraries/querydsl-core/src/main/java/com/querydsl/core`, etc.)
  - `fluentq/` — new package tree (`fluentq-libraries/fluentq-core/src/main/java/fluentq/core`, etc.)
- New top-level module directories:
  - `fluentq-libraries/` — fluentQ library modules (collections, core, guava, jpa, jpa-spring, kotlin, mongodb, r2dbc, scala, spatial, sql, sql-json, sql-spatial, sql-spring)
  - `fluentq-tooling/` — fluentQ tooling modules (apt, apt-test, codegen, codegen-utils, jpa-codegen, kotlin-codegen, ksp-codegen, maven-plugin, rewrite, sql-codegen)

### 2. New module: fluentq-rewrite
- `fluentq-tooling/fluentq-rewrite/` — OpenRewrite recipe for automated migration from Querydsl to fluentQ
- Recipe definition: `fluentq-tooling/fluentq-rewrite/src/main/resources/META-INF/rewrite/querydsl.yml`
- Migration docs: `docs/migration-8.md`

### 3. CI migration
- CircleCI → GitHub Actions (workflow files in `.github/workflows/`)
- GHA build-and-test triggered on pushes to `8.x` and `fluentq-*` branches

### 4. Turso database support
- `querydsl/querydsl-libraries/querydsl-turso/` — SQLite-compatible Turso integration

### 5. Bug fixes & cleanups unique to 8.x
- **Compat wrapper generation**: `scripts/generate-compat-wrappers.py` — generates compatibility wrappers for fluentQ migration
- **Legacy test removal**: Removed redundant legacy unit test suites from `querydsl/` to avoid duplication with fluentQ tests
- **KSP processor fix**: Removed `fluentq-ksp-codegen` dependency from `querydsl-ksp-codegen/pom.xml` to resolve duplicate KSP processor conflicts

### 6. Documentation for the transition
- `scripts/apply-upstream-patch.py` — script to apply upstream patches on 8.x when resolving merges
- Updated contributor docs: `CLAUDE.md`, `CONTRIBUTING.md`, `docs/index.md`, `docs/migration.md`

## Module structure comparison

| Category | Legacy (querydsl/) | New (fluentq/) |
|---|---|---|
| Core | querydsl-libraries/querydsl-core | fluentq-libraries/fluentq-core |
| Collections | querydsl-libraries/querydsl-collections | fluentq-libraries/fluentq-collections |
| Guava | querydsl-libraries/querydsl-guava | fluentq-libraries/fluentq-guava |
| JPA | querydsl-libraries/querydsl-jpa | fluentq-libraries/fluentq-jpa |
| JPA Spring | querydsl-libraries/querydsl-jpa-spring | fluentq-libraries/fluentq-jpa-spring |
| SQL | querydsl-libraries/querydsl-sql | fluentq-libraries/fluentq-sql |
| Kotlin | — | fluentq-libraries/fluentq-kotlin |
| MongoDB | — | fluentq-libraries/fluentq-mongodb |
| R2DBC | — | fluentq-libraries/fluentq-r2dbc |
| Scala | — | fluentq-libraries/fluentq-scala |
| Spatial | querydsl-libraries/querydsl-spatial | fluentq-libraries/fluentq-spatial |
| Apt | querydsl-tooling/querydsl-apt | fluentq-tooling/fluentq-apt |
| Codegen | querydsl-tooling/querydsl-codegen | fluentq-tooling/fluentq-codegen |
| JPA Codegen | querydsl-tooling/querydsl-jpa-codegen | fluentq-tooling/fluentq-jpa-codegen |
| Kotlin Codegen | — | fluentq-tooling/fluentq-kotlin-codegen |
| KSP Codegen | — | fluentq-tooling/fluentq-ksp-codegen |
| Maven Plugin | querydsl-tooling/querydsl-maven-plugin | fluentq-tooling/fluentq-maven-plugin |
| SQL Codegen | querydsl-tooling/querydsl-sql-codegen | fluentq-tooling/fluentq-sql-codegen |

## Branch sync notes

When merging master into 8.x, expect ~103 conflicts primarily from:
- Package path renames (`com.querydsl.` → `fluentq.`) in source files
- pom.xml version/dependency changes on both sides
- CI workflow file differences (CircleCI vs GHA)
- Documentation updates

Use `scripts/apply-upstream-patch.py` for incremental patching, then `git merge -s ours` once the patch is applied.
