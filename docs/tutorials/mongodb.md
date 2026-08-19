---
layout: default
title: Querying MongoDB
parent: Tutorials
nav_order: 3
---

# Querying MongoDB

This chapter describes the querying functionality of the MongoDB module.

## Maven Integration

Add the following dependencies to your Maven project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-mongodb</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

Configure the `maven-compiler-plugin` to run the Querydsl annotation processor:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.15.0</version>
  <configuration>
    <proc>full</proc>
    <generatedSourcesDirectory>${project.build.directory}/generated-sources/java</generatedSourcesDirectory>
    <annotationProcessorPaths>
      <path>
        <groupId>{{ site.group_id }}</groupId>
        <artifactId>querydsl-apt</artifactId>
        <version>{{ site.querydsl_version }}</version>
        <classifier>morphia</classifier>
      </path>
      <path>
        <groupId>{{ site.group_id }}</groupId>
        <artifactId>querydsl-mongodb</artifactId>
        <version>{{ site.querydsl_version }}</version>
      </path>
      <path>
        <groupId>dev.morphia.morphia</groupId>
        <artifactId>morphia-core</artifactId>
        <version>2.5.3</version>
      </path>
    </annotationProcessorPaths>
  </configuration>
</plugin>
```

The `MorphiaAnnotationProcessor` finds domain types annotated with
`com.google.code.morphia.annotations.Entity` and generates Querydsl query
types for them.

Run `mvn clean install` and your query types will be generated into
`target/generated-sources/java`.

## Querying

Querying with Querydsl MongoDB with Morphia:

```java
Morphia morphia;
Datastore datastore;
// ...
QUser user = new QUser("user");
MorphiaQuery<User> query = new MorphiaQuery<User>(morphia, datastore, user);
List<User> list = query
    .where(user.firstName.eq("Bob"))
    .fetch();
```

## General Usage

Use the cascading methods of the `MongodbQuery` class:

- **where:** Add query filters, either in varargs form separated via commas or
  cascaded via the `and` operator. Supported operations are operations
  performed on PStrings except `matches`, `indexOf`, and `charAt`.
- **orderBy:** Add ordering of the result as a varargs array of order
  expressions. Use `asc()` and `desc()` on numeric, string, and other
  comparable expressions to access `OrderSpecifier` instances.
- **limit, offset, restrict:** Set the paging of the result. `limit` for max
  results, `offset` for skipping rows, and `restrict` for defining both in one
  call.

## Ordering

```java
query
    .where(doc.title.like("*"))
    .orderBy(doc.title.asc(), doc.year.desc())
    .fetch();
```

The results are sorted ascending based on title and descending based on year.

## Limit

```java
query
    .where(doc.title.like("*"))
    .limit(10)
    .fetch();
```

## Offset

```java
query
    .where(doc.title.like("*"))
    .offset(3)
    .fetch();
```

## Geospatial Queries

Support for geospatial queries is available for `Double` typed arrays
(`Double[]`) via the `near` method:

```java
query
    .where(geoEntity.location.near(50.0, 50.0))
    .fetch();
```

## Select Only Relevant Fields

To select only relevant fields, use the overloaded projection methods
`fetch`, `iterate`, `fetchOne`, and `fetchFirst`:

```java
query
    .where(doc.title.like("*"))
    .fetch(doc.title, doc.path);
```

This query loads only the `title` and `path` fields of the documents.
