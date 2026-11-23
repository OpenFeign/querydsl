# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is **fluentQ** (formerly Querydsl) on the `8.x` release branch. Starting with version 8.0, the project has been renamed to **fluentQ**, utilizing the `fluentq.*` package prefix. Backward compatibility is maintained via generated deprecated wrappers under the `querydsl/` folder in the `com.querydsl.*` package structure.

## Build Commands

### Basic Build
```bash
./mvnw clean install
```

### Quick Build (skip tests and checks)
```bash
./mvnw -Pquickbuild clean install
```

### Profile-specific builds
```bash
# Build specific modules (e.g., jpa, sql, mongodb, or all)
./mvnw -Pquickbuild,jpa clean install
./mvnw -Pquickbuild,sql clean install
./mvnw -Pquickbuild,all clean install
```

### Test Commands
```bash
# Run tests without external databases
./mvnw -Pno-databases verify

# Run CI profile tests (excludes slow tests)
./mvnw -Pci verify

# Run development profile tests (excludes external databases and slow tests)
./mvnw -Pdev verify

# Run specific database tests
./mvnw -Pci -Dgroups=fluentq.core.testutil.MySQL verify
./mvnw -Pci -Dgroups=fluentq.core.testutil.PostgreSQL verify
```

### Code Quality
```bash
# Format code
./mvnw -Pdev initialize

# Check code format
./mvnw git-code-format:validate-code-format

# Run coverage checks
./mvnw jacoco:report
```

### Examples
```bash
# Build examples
./mvnw -Pexamples clean install

# Build specific example projects
cd querydsl-examples/querydsl-example-jpa-spring
./mvnw clean install
```

## Project Architecture

### Module Structure
- **fluentq-tooling/**: Code generation and tooling modules
  - `fluentq-apt/`: Annotation processing tools
  - `fluentq-codegen/`: Code generation utilities
  - `fluentq-sql-codegen/`: SQL schema-based code generation
  - `fluentq-kotlin-codegen/`: Kotlin code generation
  - `fluentq-ksp-codegen/`: Kotlin Symbol Processing support
  - `fluentq-maven-plugin/`: Maven plugin for code generation

- **fluentq-libraries/**: Core library modules
  - `fluentq-core/`: Core query DSL framework
  - `fluentq-jpa/`: JPA integration with Hibernate and EclipseLink support
  - `fluentq-sql/`: SQL query support for various databases
  - `fluentq-mongodb/`: MongoDB integration
  - `fluentq-collections/`: In-memory collections querying
  - `fluentq-r2dbc/`: R2DBC reactive database support
  - `fluentq-spatial/`: Spatial/GIS query extensions
  - `fluentq-kotlin/`: Kotlin language support
  - `fluentq-scala/`: Scala language support

- **querydsl/**: Generated legacy backwards compatibility wrappers (do NOT edit directly)
  - Automatically generated using `python3 scripts/generate-compat-wrappers.py` to map new `fluentq` modules back to the legacy `com.querydsl` namespaces and structure.


### Key Technologies
- **Java 17+**: Main source code (tests use Java 21)
- **Maven**: Build system with multi-module structure
- **Annotation Processing**: Code generation via APT
- **Database Support**: PostgreSQL, MySQL, Oracle, SQL Server, H2, Derby, SQLite, Firebird, CUBRID, DB2
- **JPA Providers**: Hibernate, EclipseLink
- **Testing**: JUnit 5, AssertJ, database containers for integration tests

### Code Generation Flow
1. Entity classes are annotated with `@Entity` (JPA) or `@QueryEntity` (general)
2. Annotation processors generate Q-classes (query types) at compile time
3. Q-classes provide type-safe query construction via fluent API
4. Queries are executed through backend-specific implementations

### Database Testing Strategy
- Uses Docker Compose for integration testing with real databases
- CircleCI runs parallel test jobs for each database type
- Tests are categorized by database type using JUnit 5 tags
- Embedded databases (H2, Derby, SQLite) for quick testing

## Development Profiles

- **no-databases**: Excludes all database-dependent tests
- **ci**: Excludes slow tests and performance tests (used in CI)
- **dev**: Excludes external database tests and slow tests (for local development)
- **quickbuild**: Skips tests, enforcer, and documentation generation
- **examples**: Includes example projects in build
- **release**: Adds source/javadoc jars and GPG signing

## Common Tasks

### Adding New Database Support
1. Add database driver dependency to `querydsl-sql/pom.xml`
2. Create database-specific template in `querydsl-sql/src/main/resources/`
3. Add keywords file in `querydsl-sql/src/main/resources/keywords/`
4. Update `SQLTemplates` factory methods
5. Add CircleCI job for database testing

### Code Generation
- APT processors are in `querydsl-apt/` with separate service files for each backend
- Kotlin code generation uses KSP (Kotlin Symbol Processing)
- SQL codegen reads database metadata to generate Q-classes
- Maven plugin provides `querydsl:export` goal for SQL codegen

### Testing Guidelines
- Use `@Tag` annotations to categorize tests by database or performance characteristics
- Integration tests should extend appropriate base classes from `querydsl-core/src/test/java/com/querydsl/`
- Database-specific tests go in modules like `querydsl-jpa/src/test/java/com/querydsl/jpa/`

## Documentation

Documentation lives in `docs/` and is published to https://openfeign.github.io/querydsl via GitHub Pages using Jekyll with the just-the-docs theme.

### Structure
```
docs/
├── _config.yml           # Jekyll config, theme, Liquid variables
├── Gemfile               # Ruby dependencies (local preview only)
├── index.md              # Landing page
├── introduction.md       # Background and principles
├── tutorials/            # Backend-specific tutorials (JPA, SQL, R2DBC, MongoDB, etc.)
├── guides/               # Cross-cutting guides (creating queries, result handling, codegen, aliases)
├── troubleshooting.md
└── migration.md          # Migration from defunct upstream com.querydsl
```

### Liquid Variables
Use these in Markdown files instead of hardcoding values:
- `{{ site.querydsl_version }}` — current release version (e.g. `7.1`)
- `{{ site.group_id }}` — Maven groupId (`io.github.openfeign.querydsl`)
- `{{ site.baseurl }}` — site base URL for internal links

### Deployment
- Automatic: `.github/workflows/docs.yml` triggers on push to `master` when `docs/**` changes
- Manual: `workflow_dispatch` trigger available in GitHub Actions UI

### Local Preview
```bash
docker run --rm -d -v $(pwd)/docs:/srv/jekyll -p 4000:4000 jekyll/jekyll:4.2.2 jekyll serve --host 0.0.0.0
# Open http://localhost:4000/querydsl/
```

### Editing Guidelines
- All doc files are Markdown with YAML front matter (`layout`, `title`, `parent`, `nav_order`)
- Code examples should use `maven-compiler-plugin` with `annotationProcessorPaths`, not the old `apt-maven-plugin`
- Use `{{ site.group_id }}` and `{{ site.querydsl_version }}` in dependency examples
- Dropped modules (JDO, Lucene, Hibernate Search) should not be referenced