---
layout: default
title: Troubleshooting
nav_order: 5
---

# Troubleshooting

## Insufficient Type Arguments

fluentQ needs properly encoded `List`, `Set`, `Collection`, and `Map`
properties in all code generation scenarios.

When using improperly encoded fields or getters you might see the following
stack trace:

```
java.lang.RuntimeException: Caught exception for field fluentq.jdo.testdomain.Store#products
  at fluentq.apt.Processor$2.visitType(Processor.java:117)
  at fluentq.apt.Processor$2.visitType(Processor.java:80)
  ...
Caused by: java.lang.IllegalArgumentException: Insufficient type arguments for List
  at fluentq.apt.APTTypeModel.visitDeclared(APTTypeModel.java:112)
  ...
```

Examples of problematic field declarations and their corrections:

```java
private Collection names;                      // WRONG
private Collection<String> names;              // RIGHT

private Map employeesByName;                   // WRONG
private Map<String, Employee> employeesByName; // RIGHT
```

## Multithreaded Initialization of fluentQ Q-types

When fluentQ Q-types are initialized from multiple threads, deadlocks can
occur if the Q-types have circular dependencies.

The solution is to initialize the classes in a single thread before they are
used in different threads.

The `fluentq.codegen.ClassPathUtils` class can be used for that:

```java
ClassPathUtils.scanPackage(Thread.currentThread().getContextClassLoader(), packageToLoad);
```

Replace `packageToLoad` with the package of the classes you want to initialize.
