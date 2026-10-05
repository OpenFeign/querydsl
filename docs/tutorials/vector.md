---
layout: default
title: Querying Vectors
parent: Tutorials
nav_order: 10
---

# Querydsl Vector

The Vector modules add type-safe similarity search over embedding columns.
Vectors are mapped to `float[]` and exposed as `VectorPath` in query types.

| Database | SQL templates | JPA (Hibernate) |
|:---------|:--------------|:----------------|
| PostgreSQL + [pgvector](https://github.com/pgvector/pgvector) | `PGvectorTemplates` | yes |
| Oracle 23ai | `OracleVectorTemplates` | yes |

## Operations

| Method | pgvector | Oracle |
|:-------|:---------|:-------|
| `l2Distance` | `<->` | `VECTOR_DISTANCE(.., EUCLIDEAN)` |
| `l2SquaredDistance` | `(<->)^2` | `VECTOR_DISTANCE(.., EUCLIDEAN_SQUARED)` |
| `cosineDistance` | `<=>` | `VECTOR_DISTANCE(.., COSINE)` |
| `innerProduct` | `(<#>) * -1` | `VECTOR_DISTANCE(.., DOT) * -1` |
| `negativeInnerProduct` | `<#>` | `VECTOR_DISTANCE(.., DOT)` |
| `l1Distance` | `<+>` | `VECTOR_DISTANCE(.., MANHATTAN)` |
| `dims` | `vector_dims` | `VECTOR_DIMENSION_COUNT` |
| `norm` | `vector_norm` | `VECTOR_NORM` |

Each distance accepts either a `float[]` value or another vector expression.
`VectorExpressions.vector(float[])` turns a value into an expression, for example
`vector(question).dims()`. In JPA, a constant has to be compared with a vector
property, because Hibernate cannot infer the type of a standalone array parameter.
Order by the distance ascending to get the nearest neighbours first. On
pgvector, the operator form lets PostgreSQL use HNSW and IVFFlat indexes.

## SQL

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-sql-vector</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

Use `PGvectorTemplates` or `OracleVectorTemplates` instead of the plain
dialect templates:

```java
SQLQueryFactory queryFactory =
    new SQLQueryFactory(new Configuration(new PGvectorTemplates()), dataSource);

float[] question = embeddingModel.embed("how do I reset my password?");
List<String> titles = queryFactory
    .select(document.title)
    .from(document)
    .orderBy(document.embedding.cosineDistance(question).asc())
    .limit(5)
    .fetch();
```

To combine vectors with another extension, such as PostGIS, add the operators
from `VectorTemplatesSupport` and the `PGvectorType` custom type to your own
`SQLTemplates` subclass.

### Code Generation

With `querydsl-sql-vector` on the `querydsl-maven-plugin` classpath, `vector`
columns are generated as `VectorPath<float[]>` and query types extend
`RelationalPathVector`:

```xml
<plugin>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-maven-plugin</artifactId>
  <version>{{ site.querydsl_version }}</version>
  ...
  <dependencies>
    <dependency>
      <groupId>{{ site.group_id }}</groupId>
      <artifactId>querydsl-sql-vector</artifactId>
      <version>{{ site.querydsl_version }}</version>
    </dependency>
  </dependencies>
</plugin>
```

## JPA

Map the column with [Hibernate Vector](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#vector-module)
and add `querydsl-vector` to the annotation processor path:

```java
@Entity
public class Document {
  @Id Long id;
  String title;

  @JdbcTypeCode(SqlTypes.VECTOR)
  @Array(length = 1536)
  float[] embedding;
}
```

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <annotationProcessorPaths>
      <path>
        <groupId>{{ site.group_id }}</groupId>
        <artifactId>querydsl-apt</artifactId>
        <version>{{ site.querydsl_version }}</version>
        <classifier>jpa</classifier>
      </path>
      <path>
        <groupId>{{ site.group_id }}</groupId>
        <artifactId>querydsl-vector</artifactId>
        <version>{{ site.querydsl_version }}</version>
      </path>
    </annotationProcessorPaths>
  </configuration>
</plugin>
```

With `querydsl-vector` on the processor path, every `float[]` property is
generated as a `VectorPath`. Add `org.hibernate.orm:hibernate-vector` and
`querydsl-vector` at runtime and `HQLTemplates` renders the
`hibernate-vector` functions:

```java
List<Document> nearest = queryFactory
    .selectFrom(document)
    .orderBy(document.embedding.l2Distance(question).asc())
    .limit(10)
    .fetch();
```
