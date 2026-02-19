---
layout: default
title: Home
nav_order: 1
---

# Querydsl Reference Documentation
{: .fs-9 }

Type-safe SQL-like queries for Java.
{: .fs-6 .fw-300 }

[Get Started with JPA]({{ site.baseurl }}/tutorials/jpa){: .btn .btn-primary .fs-5 .mb-4 .mb-md-0 .mr-2 }
[View on GitHub](https://github.com/OpenFeign/querydsl){: .btn .fs-5 .mb-4 .mb-md-0 }

---

Querydsl is a framework that enables the construction of statically typed
SQL-like queries for multiple backends in Java. Instead of writing queries as
inline strings or externalizing them into XML files, you construct them via a
fluent API.

## Benefits

- **Code completion in your IDE** — discover available columns and operations as
  you type.
- **Syntactically valid queries** — the compiler catches most query mistakes
  before runtime.
- **Safe domain references** — properties are referenced through generated
  types, not strings.
- **Refactoring friendly** — rename a field and every query that uses it updates
  automatically.

## Supported Backends

| Module | Artifact |
|:-------|:---------|
| [JPA (Hibernate / EclipseLink)]({{ site.baseurl }}/tutorials/jpa) | `{{ site.group_id }}:querydsl-jpa` |
| [SQL (JDBC)]({{ site.baseurl }}/tutorials/sql) | `{{ site.group_id }}:querydsl-sql` |
| [R2DBC (Reactive SQL)]({{ site.baseurl }}/tutorials/r2dbc) | `{{ site.group_id }}:querydsl-r2dbc` |
| [MongoDB]({{ site.baseurl }}/tutorials/mongodb) | `{{ site.group_id }}:querydsl-mongodb` |
| [Collections]({{ site.baseurl }}/tutorials/collections) | `{{ site.group_id }}:querydsl-collections` |
| [Spatial]({{ site.baseurl }}/tutorials/spatial) | `{{ site.group_id }}:querydsl-sql-spatial` |
| [Kotlin Extensions]({{ site.baseurl }}/tutorials/kotlin) | `{{ site.group_id }}:querydsl-kotlin` |
| [Scala Extensions]({{ site.baseurl }}/tutorials/scala) | `{{ site.group_id }}:querydsl-scala` |

## Quick Example

```java
QCustomer customer = QCustomer.customer;
List<Customer> bobs = queryFactory.selectFrom(customer)
    .where(customer.firstName.eq("Bob"))
    .orderBy(customer.lastName.asc())
    .fetch();
```

## Current Version

The latest release is **{{ site.querydsl_version }}**. Add it to your Maven
project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

See the [Migration Guide]({{ site.baseurl }}/migration) if you are upgrading
from the original `com.querydsl` artifacts.
