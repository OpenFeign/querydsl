---
layout: default
title: Querying Collections
parent: Tutorials
nav_order: 4
---

# Querying Collections

The `fluentq-collections` module can be used with generated query types or
without. The first section describes usage without generated query types.

## Usage Without Generated Query Types

To use `fluentq-collections` without generated query types, use the fluentQ
alias feature.

Add the following static imports:

```java
// needed for access of the fluentQ Collections API
import static fluentq.collections.CollQueryFactory.*;
// needed if you use the $-invocations
import static fluentq.core.alias.Alias.*;
```

Create an alias instance for the `Cat` class. Alias instances can only be
created for non-final classes with an empty constructor.

The alias instance and its getter invocations are transformed into paths by
wrapping them in dollar-method invocations. The call `c.getKittens()` is
internally transformed into the property path `c.kittens` inside the dollar
method.

```java
Cat c = alias(Cat.class, "cat");
for (String name : select($(c.getName())).from($(c),cats)
  .where($(c.getKittens()).size().gt(0))
  .fetch()) {
    System.out.println(name);
}
```

The following example is a variation where the access to the list size happens
inside the dollar-method invocation:

```java
Cat c = alias(Cat.class, "cat");
for (String name : select($(c.getName())).from($(c),cats)
  .where($(c.getKittens().size()).gt(0))
  .fetch()) {
    System.out.println(name);
}
```

All non-primitive and non-final typed properties of aliases are aliases
themselves. You may cascade method calls until you hit a primitive or final
type in the dollar-method scope, e.g.:

```java
$(c.getMate().getName())
```

is transformed into `c.mate.name` internally, but:

```java
$(c.getMate().getName().toLowerCase())
```

is not transformed properly, since the `toLowerCase()` invocation is not
tracked.

You may only invoke getters, `size()`, `contains(Object)`, and `get(int)` on
alias types. All other invocations throw exceptions.

## Usage With Generated Query Types

The same query expressed with generated expression types:

```java
QCat cat = new QCat("cat");
for (String name : select(cat.name).from(cat,cats)
  .where(cat.kittens.size().gt(0))
  .fetch()) {
    System.out.println(name);
}
```

When you use generated query types, you instantiate expressions instead of
alias instances and use the property paths directly without any dollar-method
wrapping.

## Maven Integration

Add the following dependencies to your Maven project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>fluentq-collections</artifactId>
  <version>{{ site.fluentq_version }}</version>
</dependency>
```

If you are not using JPA you can generate expression types for your domain
types by annotating them with `fluentq.core.annotations.QueryEntity` and
configuring the `maven-compiler-plugin`:

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <generatedSourcesDirectory>target/generated-sources/java</generatedSourcesDirectory>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>{{ site.group_id }}</groupId>
      <artifactId>fluentq-apt</artifactId>
      <version>{{ site.fluentq_version }}</version>
      <classifier>general</classifier>
    </dependency>
  </dependencies>
</plugin>
```

## Hamcrest Matchers

fluentQ Collections provides Hamcrest matchers:

```java
import static org.hamcrest.core.IsEqual.equalTo;
import static fluentq.collections.PathMatcher.hasValue;
import static org.junit.Assert.assertThat;

Car car = new Car();
car.setHorsePower(123);

assertThat(car, hasValue($.horsePower));
assertThat(car, hasValue($.horsePower, equalTo(123)));
```

## Usage With the Eclipse Compiler for Java

If `fluentq-collections` is used with a JRE where the system compiler is not
available, `CollQuery` instances can be configured to use the Eclipse Compiler
for Java (ECJ) instead:

```java
DefaultEvaluatorFactory evaluatorFactory = new DefaultEvaluatorFactory(
    CollQueryTemplates.DEFAULT,
    new ECJEvaluatorFactory(getClass().getClassLoader()));
QueryEngine queryEngine = new DefaultQueryEngine(evaluatorFactory);
CollQuery query = new CollQuery(queryEngine);
```
