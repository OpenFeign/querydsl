---
layout: default
title: Alias Usage
parent: Guides
nav_order: 4
---

# Alias Usage

In cases where code generation is not an option, alias objects can be used as
path references for expression construction. They work via proxied Java Bean
objects through getter method invocations.

The following examples demonstrate how alias objects can be used as
replacements for expression creation based on generated types.

First, an example query with APT-generated domain types:

```java
QCat cat = new QCat("cat");
for (String name : queryFactory.select(cat.name).from(cat,cats)
    .where(cat.kittens.size().gt(0))
    .fetch()) {
    System.out.println(name);
}
```

And now with an alias instance for the `Cat` class. The call
`c.getKittens()` inside the dollar-method is internally transformed into the
property path `c.kittens`.

```java
Cat c = alias(Cat.class, "cat");
for (String name : select($(c.getName())).from($(c),cats)
    .where($(c.getKittens()).size().gt(0))
    .fetch()) {
    System.out.println(name);
}
```

To use the alias functionality, add the following two imports:

```java
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
```

The following example is a variation where the access to the list size happens
inside the dollar-method invocation:

```java
Cat c = alias(Cat.class, "cat");
for (String name : queryFactory.select($(c.getName())).from($(c),cats)
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
