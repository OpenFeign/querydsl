---
layout: default
title: Querying Hibernate
parent: Tutorials
nav_order: 6
---

# Querying Hibernate

Querydsl provides Hibernate-specific extensions on top of the standard JPA
support. For common features such as basic querying, joins, subqueries, and DML
operations, see the [JPA tutorial]({{ site.baseurl }}/tutorials/jpa).

This page covers features exclusive to the Hibernate API.

## Maven Integration

The Hibernate integration uses the same `querydsl-jpa` artifact:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

If your domain model uses Hibernate-specific annotations, configure the
annotation processor with `HibernateAnnotationProcessor`:

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

For standard JPA annotations, `JPAAnnotationProcessor` works as well. See the
[JPA tutorial]({{ site.baseurl }}/tutorials/jpa) for details.

## Creating Queries

Use `HibernateQuery` with a Hibernate `Session`:

```java
HibernateQuery<?> query = new HibernateQuery<Void>(session);
```

Or use `HibernateQueryFactory` as the recommended approach:

```java
HibernateQueryFactory queryFactory = new HibernateQueryFactory(session);
```

All standard JPQL query operations (`from`, `where`, `join`, `groupBy`,
`orderBy`, etc.) work the same way as described in the
[JPA tutorial]({{ site.baseurl }}/tutorials/jpa).

`HibernateQuery` also supports `StatelessSession`:

```java
HibernateQuery<?> query = new HibernateQuery<Void>(statelessSession);
```

## Hibernate-Specific Query Options

`HibernateQuery` provides several options not available on `JPAQuery`.

### Query Caching

```java
List<Cat> cats = queryFactory.selectFrom(cat)
    .where(cat.name.startsWith("A"))
    .setCacheable(true)
    .setCacheRegion("catCache")
    .fetch();
```

### Read-Only Mode

Entities loaded in read-only mode are never dirty-checked:

```java
List<Cat> cats = queryFactory.selectFrom(cat)
    .setReadOnly(true)
    .fetch();
```

### SQL Comments

```java
List<Cat> cats = queryFactory.selectFrom(cat)
    .setComment("load cats by name")
    .fetch();
```

### Lock Modes

```java
List<Cat> cats = queryFactory.selectFrom(cat)
    .setLockMode(cat, LockMode.PESSIMISTIC_WRITE)
    .fetch();
```

### Flush Mode

```java
List<Cat> cats = queryFactory.selectFrom(cat)
    .setFlushMode(FlushMode.AUTO)
    .fetch();
```

### Fetch Size and Timeout

```java
List<Cat> cats = queryFactory.selectFrom(cat)
    .setFetchSize(50)
    .setTimeout(30)
    .fetch();
```

### Scrollable Results

```java
ScrollableResults results = queryFactory.selectFrom(cat)
    .createQuery()
    .scroll(ScrollMode.FORWARD_ONLY);
```

## Exposing the Original Query

To access the underlying Hibernate `Query` directly:

```java
Query hibernateQuery = queryFactory.selectFrom(cat).createQuery();
List results = hibernateQuery.list();
```

This returns a Hibernate `org.hibernate.query.Query` rather than a JPA
`jakarta.persistence.Query`.

## Native SQL with Hibernate

Use `HibernateSQLQuery` to run native SQL through a Hibernate `Session`:

```java
SQLTemplates templates = new H2Templates();
HibernateSQLQuery<?> query = new HibernateSQLQuery<Void>(session, templates);
List<String> names = query.select(cat.name).from(cat).fetch();
```

See the [JPA tutorial]({{ site.baseurl }}/tutorials/jpa#using-native-sql-in-jpa-queries)
for more details on native SQL query patterns.

## Comparison with JPAQuery

| Feature | JPAQuery | HibernateQuery |
|---|---|---|
| Underlying API | JPA EntityManager | Hibernate Session |
| Query factory | `JPAQueryFactory` | `HibernateQueryFactory` |
| Query caching | Not available | `setCacheable()`, `setCacheRegion()` |
| Read-only mode | Not available | `setReadOnly()` |
| SQL comments | Not available | `setComment()` |
| Lock modes | JPA lock modes | Hibernate `LockMode` per path |
| Flush mode | Not available | `setFlushMode()` |
| Scrollable results | Not available | `scroll(ScrollMode)` |
| StatelessSession | Not available | Supported |
| Native SQL class | `JPASQLQuery` | `HibernateSQLQuery` |
