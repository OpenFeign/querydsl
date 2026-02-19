---
layout: default
title: Troubleshooting
nav_order: 5
---

# Troubleshooting

## Insufficient Type Arguments

Querydsl needs properly encoded `List`, `Set`, `Collection`, and `Map`
properties in all code generation scenarios.

When using improperly encoded fields or getters you might see the following
stack trace:

```
java.lang.RuntimeException: Caught exception for field com.querydsl.jdo.testdomain.Store#products
  at com.querydsl.apt.Processor$2.visitType(Processor.java:117)
  at com.querydsl.apt.Processor$2.visitType(Processor.java:80)
  ...
Caused by: java.lang.IllegalArgumentException: Insufficient type arguments for List
  at com.querydsl.apt.APTTypeModel.visitDeclared(APTTypeModel.java:112)
  ...
```

Examples of problematic field declarations and their corrections:

```java
private Collection names;                      // WRONG
private Collection<String> names;              // RIGHT

private Map employeesByName;                   // WRONG
private Map<String, Employee> employeesByName; // RIGHT
```

## Multithreaded Initialization of Querydsl Q-types

When Querydsl Q-types are initialized from multiple threads, deadlocks can
occur if the Q-types have circular dependencies.

The solution is to initialize the classes in a single thread before they are
used in different threads.

The `com.querydsl.codegen.ClassPathUtils` class can be used for that:

```java
ClassPathUtils.scanPackage(Thread.currentThread().getContextClassLoader(), packageToLoad);
```

Replace `packageToLoad` with the package of the classes you want to initialize.
