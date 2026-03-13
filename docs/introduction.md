---
layout: default
title: Introduction
nav_order: 2
---

# Introduction

## Background

Querydsl was born out of the need to maintain HQL queries in a type-safe way.
Building HQL queries incrementally requires string concatenation, which
produces hard-to-read code. Referencing domain types and properties through
plain strings is fragile and error-prone.

With a changing domain model, type safety brings huge benefits. Domain changes
are reflected directly in queries, and IDE auto-complete makes query
construction faster and safer.

HQL for Hibernate was the first target language for Querydsl. Today the
framework supports **JPA**, **SQL (JDBC)**, **R2DBC**, **MongoDB**,
**Lucene**, **Collections**, **Spatial**, **Kotlin**, and **Scala** as backends.

If you are new to database access in Java,
[this guide](https://www.marcobehler.com/guides/a-guide-to-accessing-databases-in-java)
provides a good overview of the various parts and options and shows where
Querydsl fits in.

## Principles

**Type safety** is the core principle of Querydsl. Queries are constructed
based on generated query types that reflect the properties of your domain
types. Function and method invocations are constructed in a fully type-safe
manner.

**Consistency** is another important principle. The query paths and operations
are the same across all implementations, and the query interfaces share a
common base interface.

To get an impression of the expressivity of the Querydsl query and expression
types, explore the Javadocs for `com.querydsl.core.Query`,
`com.querydsl.core.Fetchable`, and `com.querydsl.core.types.Expression`.
