---
layout: default
title: Querying R2DBC
parent: Tutorials
nav_order: 5
---

# Querying R2DBC

The `querydsl-r2dbc` module provides reactive, non-blocking database access
built on [R2DBC](https://r2dbc.io/) and [Project Reactor](https://projectreactor.io/).
The API mirrors the SQL module but returns `Mono` and `Flux` types instead of
blocking results.

## Maven Integration

Add the following dependencies to your Maven project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-r2dbc</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

You also need an R2DBC driver for your database, for example:

```xml
<!-- PostgreSQL R2DBC driver -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>r2dbc-postgresql</artifactId>
</dependency>
```

Code generation for the query types works exactly like the
[SQL module]({{ site.baseurl }}/tutorials/sql#code-generation-via-maven) — use the
`querydsl-maven-plugin` or `querydsl-sql-codegen` to export your schema.

## Configuration

Configuration is done via `com.querydsl.r2dbc.Configuration`, which takes a
Querydsl SQL dialect as an argument:

```java
SQLTemplates templates = new PostgreSQLTemplates();
Configuration configuration = new Configuration(templates);
```

The same `SQLTemplates` classes used by the SQL module are reused for R2DBC.
See the [SQL tutorial]({{ site.baseurl }}/tutorials/sql#configuration) for the
full list of available dialects.

## Connection Provider

R2DBC queries require an `R2DBCConnectionProvider` to obtain reactive
connections. The simplest way to create one is from a `ConnectionFactory`:

```java
ConnectionFactory connectionFactory = ...;
R2DBCConnectionProvider provider = R2DBCConnectionProvider.from(connectionFactory);
```

The provider manages the connection lifecycle, automatically acquiring and
releasing connections for each operation.

## Creating the Query Factory

`R2DBCQueryFactory` is the main entry point for constructing queries:

```java
R2DBCQueryFactory queryFactory = new R2DBCQueryFactory(configuration, provider);
```

Database-specific query factories are also available:

- `R2DBCPostgreQueryFactory` — PostgreSQL-specific features
- `R2DBCMySQLQueryFactory` — MySQL-specific features (e.g. `INSERT IGNORE`,
  `ON DUPLICATE KEY UPDATE`)
- `R2DBCServerQueryFactory` — SQL Server-specific features

```java
R2DBCPostgreQueryFactory queryFactory = new R2DBCPostgreQueryFactory(provider);
```

## Querying

Queries return reactive types from Project Reactor.

### Fetching Multiple Results

```java
QCustomer customer = QCustomer.customer;

Flux<String> lastNames = queryFactory.select(customer.lastName)
    .from(customer)
    .where(customer.firstName.eq("Bob"))
    .fetch();

lastNames.subscribe(name -> System.out.println(name));
```

### Fetching a Single Result

```java
Mono<Customer> bob = queryFactory.selectFrom(customer)
    .where(customer.firstName.eq("Bob"))
    .fetchFirst();
```

### Using Tuples

```java
Flux<Tuple> rows = queryFactory.select(customer.firstName, customer.lastName)
    .from(customer)
    .fetch();

rows.subscribe(row -> {
    System.out.println(row.get(customer.firstName));
    System.out.println(row.get(customer.lastName));
});
```

## Joins

Joins work the same way as in the SQL module:

```java
QCustomer customer = QCustomer.customer;
QCompany company = QCompany.company;

Flux<Tuple> results = queryFactory
    .select(customer.firstName, customer.lastName, company.name)
    .from(customer)
    .innerJoin(customer.company, company)
    .fetch();
```

## Ordering and Paging

```java
Flux<Customer> results = queryFactory.selectFrom(customer)
    .orderBy(customer.lastName.asc(), customer.firstName.asc())
    .limit(10)
    .offset(20)
    .fetch();
```

## Subqueries

```java
QCustomer customer = QCustomer.customer;
QCustomer customer2 = new QCustomer("customer2");

Flux<Tuple> results = queryFactory.select(customer.all())
    .from(customer)
    .where(customer.status.eq(
        SQLExpressions.select(customer2.status.max()).from(customer2)))
    .fetch();
```

## Data Manipulation Commands

### Insert

```java
QSurvey survey = QSurvey.survey;

Mono<Long> insertCount = queryFactory.insert(survey)
    .columns(survey.id, survey.name)
    .values(3, "Hello")
    .execute();
```

Using the `set` method:

```java
Mono<Long> insertCount = queryFactory.insert(survey)
    .set(survey.id, 3)
    .set(survey.name, "Hello")
    .execute();
```

### Update

```java
Mono<Long> updateCount = queryFactory.update(survey)
    .where(survey.name.eq("XXX"))
    .set(survey.name, "S")
    .execute();
```

### Delete

```java
Mono<Long> deleteCount = queryFactory.delete(survey)
    .where(survey.name.eq("XXX"))
    .execute();
```

## MySQL-Specific Features

The `R2DBCMySQLQueryFactory` provides MySQL-specific insert variants:

```java
R2DBCMySQLQueryFactory mysqlFactory = new R2DBCMySQLQueryFactory(configuration, provider);

// INSERT IGNORE
mysqlFactory.insertIgnore(survey)
    .set(survey.id, 3)
    .set(survey.name, "Hello")
    .execute();

// INSERT ... ON DUPLICATE KEY UPDATE
mysqlFactory.insertOnDuplicateKeyUpdate(survey, "name = VALUES(name)")
    .set(survey.id, 3)
    .set(survey.name, "Hello")
    .execute();
```

## Comparison with the SQL Module

| Feature | SQL Module | R2DBC Module |
|:--------|:-----------|:-------------|
| Return types | `List<T>`, `T` | `Flux<T>`, `Mono<T>` |
| Connection | `java.sql.Connection` | `io.r2dbc.spi.Connection` |
| Factory class | `SQLQueryFactory` | `R2DBCQueryFactory` |
| Query class | `SQLQuery` | `R2DBCQuery` |
| Blocking | Yes | No |
| Configuration | `com.querydsl.sql.Configuration` | `com.querydsl.r2dbc.Configuration` |
| Templates | Shared `SQLTemplates` | Shared `SQLTemplates` |
