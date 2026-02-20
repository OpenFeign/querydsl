---
layout: default
title: Querying in Kotlin
parent: Tutorials
nav_order: 8
---

# Querying in Kotlin

The `querydsl-kotlin` module provides Kotlin extension functions that make
Querydsl expressions feel natural in Kotlin code. Operator overloading allows
you to write queries using standard Kotlin operators.

## Maven Integration

Add the following dependency to your Maven project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-kotlin</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

You also need the Querydsl module for your backend (e.g. `querydsl-jpa`,
`querydsl-sql`). Code generation works the same as with Java — use the
annotation processor for JPA or the Maven plugin for SQL.

## Kotlin Operator Extensions

The `querydsl-kotlin` module provides operator overloads for Querydsl
expressions, enabling idiomatic Kotlin syntax.

### Boolean Operations

```kotlin
import com.querydsl.kotlin.*

val customer = QCustomer.customer

// Standard Querydsl          Kotlin alternative
customer.active.not()       // !customer.active
customer.active
  .and(customer.verified)   // customer.active and customer.verified
customer.active
  .or(customer.verified)    // customer.active or customer.verified
```

### Comparison Operations

```kotlin
// Standard Querydsl          Kotlin alternative
customer.age.lt(5)          // customer.age < 5    (not supported as operator)
customer.age.loe(5)         // customer.age <= 5   (not supported as operator)
customer.age.gt(5)          // customer.age > 5    (not supported as operator)
customer.age.goe(5)         // customer.age >= 5   (not supported as operator)
customer.age.negate()       // -customer.age
```

### Numeric Operations

```kotlin
// Standard Querydsl              Kotlin alternative
customer.age.add(3)             // customer.age + 3
customer.age.subtract(3)        // customer.age - 3
customer.age.multiply(3)        // customer.age * 3
customer.age.divide(3)          // customer.age / 3
customer.age.mod(5)             // customer.age % 5
```

These operators work with both expressions and literal values:

```kotlin
// With another expression
customer.age + customer.bonusYears

// With a literal
customer.age + 3
```

### String Operations

```kotlin
// Standard Querydsl                    Kotlin alternative
customer.firstName.append("X")       // customer.firstName + "X"
customer.firstName.append(suffix)    // customer.firstName + suffix
customer.firstName.charAt(0)         // customer.firstName[0]
```

## Example Queries

### JPA with Kotlin

```kotlin
val customer = QCustomer.customer

// Simple query
val bobs: List<Customer> = queryFactory.selectFrom(customer)
    .where(customer.firstName.eq("Bob"))
    .fetch()

// Using Kotlin operators for complex conditions
val results = queryFactory.selectFrom(customer)
    .where(
        customer.firstName.eq("Bob")
            .and(customer.age + 5 > customer.minAge)
    )
    .orderBy(customer.lastName.asc())
    .fetch()
```

### SQL with Kotlin

```kotlin
val employee = QEmployee.employee

val names = queryFactory.select(employee.firstName + " " + employee.lastName)
    .from(employee)
    .where(employee.salary * 12 > 100_000)
    .fetch()
```

## Using with Other Backends

The Kotlin extension functions work with any Querydsl backend — JPA, SQL,
R2DBC, MongoDB, or Collections. Import `com.querydsl.kotlin.*` and the
operators become available on all expression types.
