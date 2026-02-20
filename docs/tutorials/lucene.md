---
layout: default
title: Querying Lucene
parent: Tutorials
nav_order: 6
---

# Querying Lucene

This chapter describes the querying functionality of the Lucene modules.

## Maven Integration

Two modules are available depending on your Lucene version:

**Lucene 9** (Java 17+):

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-lucene9</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

**Lucene 10** (Java 21+):

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-lucene10</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

Both modules provide the same API. The examples below use the `com.querydsl.lucene9`
package — replace with `com.querydsl.lucene10` if you are on Lucene 10.

## Creating the Query Types

Since Lucene has no schema, query types are created manually. For a document
with `year` and `title` fields the query type looks like this:

```java
public class QDocument extends EntityPathBase<Document> {
    private static final long serialVersionUID = -4872833626508344081L;

    public QDocument(String var) {
        super(Document.class, PathMetadataFactory.forVariable(var));
    }

    public final StringPath year = createString("year");

    public final StringPath title = createString("title");
}
```

`QDocument` represents a Lucene document with the fields `year` and `title`.

Code generation is not available for Lucene since no schema data is available.

## Querying

Querying with Querydsl Lucene is straightforward:

```java
QDocument doc = new QDocument("doc");

IndexSearcher searcher = new IndexSearcher(index);
LuceneQuery query = new LuceneQuery(searcher);
List<Document> documents = query
    .where(doc.year.between("1800", "2000").and(doc.title.startsWith("Huckle")))
    .fetch();
```

This is transformed into the following Lucene query:

```
+year:[1800 TO 2000] +title:huckle*
```

### Custom Serializer

The default `LuceneSerializer` does not lowercase terms and splits on
whitespace. To customize this behavior, pass a serializer to the query:

```java
LuceneSerializer serializer = new LuceneSerializer(true, true);
LuceneQuery query = new LuceneQuery(serializer, searcher);
```

The constructor parameters are:

| Parameter | Description |
|:----------|:------------|
| `lowerCase` | Convert search terms to lowercase |
| `splitTerms` | Split terms by whitespace into multi-term queries |

## Typed Queries

Use `TypedQuery` to transform Lucene documents into custom types:

```java
TypedQuery<Person> query = new TypedQuery<>(searcher, doc -> {
    Person person = new Person();
    person.setName(doc.get("name"));
    person.setAge(Integer.parseInt(doc.get("age")));
    return person;
});
List<Person> results = query.where(doc.title.eq("Engineer")).fetch();
```

## General Usage

Use the cascading methods of the `LuceneQuery` class:

**where:** Add query filters, either in varargs form separated via commas or
cascaded via the and-operator. Supported operations include equality, inequality,
range queries, string matching (`like`, `startsWith`, `endsWith`, `contains`),
and collection operations (`in`, `notIn`).

**orderBy:** Add ordering of the result as a varargs array of order
expressions. Use `asc()` and `desc()` on numeric, string, and other comparable
expressions to access the `OrderSpecifier` instances.

**limit, offset, restrict:** Set the paging of the result. `limit` for max
results, `offset` for skipping rows, and `restrict` for defining both in one
call.

**load:** Select specific fields to load from the index instead of loading
the entire document.

## Ordering

```java
query
    .where(doc.title.like("*"))
    .orderBy(doc.title.asc(), doc.year.desc())
    .fetch();
```

This is equivalent to the Lucene query `title:*` with results sorted ascending
by title and descending by year.

Alternatively, use a `Sort` instance directly:

```java
Sort sort = ...;
query
    .where(doc.title.like("*"))
    .sort(sort)
    .fetch();
```

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

## Field Selection

Load only specific fields from the index:

```java
query
    .where(doc.title.ne(""))
    .load(doc.title)
    .fetch();
```

## Fuzzy Searches

Fuzzy searches can be expressed via `fuzzyLike` methods in the
`LuceneExpressions` class:

```java
query
    .where(LuceneExpressions.fuzzyLike(doc.title, "Hello"))
    .fetch();
```

You can also control the maximum edit distance and prefix length:

```java
query
    .where(LuceneExpressions.fuzzyLike(doc.title, "Hello", 2, 0))
    .fetch();
```

## Applying Lucene Filters

Apply a native Lucene `Query` as a filter:

```java
query
    .where(doc.title.like("*"))
    .filter(IntPoint.newExactQuery("year", 1990))
    .fetch();
```
