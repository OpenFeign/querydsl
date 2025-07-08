# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is Querydsl, a framework for constructing type-safe SQL-like queries for multiple backends including JPA, MongoDB, and SQL in Java. This is a fork under OpenFeign maintaining the project with regular releases since the original project became stale.

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
./mvnw -Pci -Dgroups=com.querydsl.core.testutil.MySQL verify
./mvnw -Pci -Dgroups=com.querydsl.core.testutil.PostgreSQL verify
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
- **querydsl-tooling/**: Code generation and tooling modules
  - `querydsl-apt/`: Annotation processing tools
  - `querydsl-codegen/`: Code generation utilities
  - `querydsl-sql-codegen/`: SQL schema-based code generation
  - `querydsl-kotlin-codegen/`: Kotlin code generation
  - `querydsl-ksp-codegen/`: Kotlin Symbol Processing support
  - `querydsl-maven-plugin/`: Maven plugin for code generation

- **querydsl-libraries/**: Core library modules
  - `querydsl-core/`: Core query DSL framework
  - `querydsl-jpa/`: JPA integration with Hibernate and EclipseLink support
  - `querydsl-sql/`: SQL query support for various databases
  - `querydsl-mongodb/`: MongoDB integration
  - `querydsl-collections/`: In-memory collections querying
  - `querydsl-r2dbc/`: R2DBC reactive database support
  - `querydsl-spatial/`: Spatial/GIS query extensions
  - `querydsl-kotlin/`: Kotlin language support
  - `querydsl-scala/`: Scala language support

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