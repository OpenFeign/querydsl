---
layout: default
title: Creating Queries
parent: Guides
nav_order: 1
---

# Creating Queries

Query construction in Querydsl involves calling query methods with expression
arguments. Since query methods are mostly module-specific and have already been
presented in the tutorial section, this part focuses on expressions.

Expressions are normally constructed by accessing fields and calling methods on
the generated expression types of your domain module. For cases where code
generation is not applicable, generic ways to construct expressions can be used
instead.

## Complex Predicates

To construct complex boolean expressions, use the
`com.querydsl.core.BooleanBuilder` class. It implements `Predicate` and can be
used in cascaded form:

```java
public List<Customer> getCustomer(String... names) {
    QCustomer customer = QCustomer.customer;
    JPAQuery<Customer> query = queryFactory.selectFrom(customer);
    BooleanBuilder builder = new BooleanBuilder();
    for (String name : names) {
        builder.or(customer.name.eq(name));
    }
    query.where(builder); // customer.name eq name1 OR customer.name eq name2 OR ...
    return query.fetch();
}
```

`BooleanBuilder` is mutable and represents initially `null`. After each `and`
or `or` call it holds the result of the operation.

## Dynamic Expressions

The `com.querydsl.core.types.dsl.Expressions` class is a static factory class
for dynamic expression construction. The factory methods are named by the
returned type and are mostly self-documenting.

In general, the `Expressions` class should only be used when the fluent DSL
forms are not available, such as dynamic paths, custom syntax, or custom
operations.

The following expression:

```java
QPerson person = QPerson.person;
person.firstName.startsWith("P");
```

could be constructed like this if Q-types are not available:

```java
Path<Person> person = Expressions.path(Person.class, "person");
Path<String> personFirstName = Expressions.path(String.class, person, "firstName");
Constant<String> constant = Expressions.constant("P");
Expressions.predicate(Ops.STARTS_WITH, personFirstName, constant);
```

`Path` instances represent variables and properties, `Constant` instances are
constants, `Operation` instances are operations, and `TemplateExpression`
instances can be used to express expressions as string templates.

## Dynamic Paths

In addition to the `Expressions`-based expression creation, Querydsl provides
a more fluent API for dynamic path creation.

For dynamic path generation, the `com.querydsl.core.types.dsl.PathBuilder`
class can be used. It extends `EntityPathBase` and can be used as an
alternative to class generation and alias usage for path generation.

Compared to the `Expressions` API, `PathBuilder` does not provide direct
support for unknown operations or custom syntax, but the syntax is closer to
the normal DSL.

String property:

```java
PathBuilder<User> entityPath = new PathBuilder<User>(User.class, "entity");
// fully generic access
entityPath.get("userName");
// .. or with supplied type
entityPath.get("userName", String.class);
// .. and correct signature
entityPath.getString("userName").lower();
```

List property with component type:

```java
entityPath.getList("list", String.class).get(0);
```

Using a component expression type:

```java
entityPath.getList("list", String.class, StringPath.class).get(0).lower();
```

Map property with key and value type:

```java
entityPath.getMap("map", String.class, String.class).get("key");
```

Using a component expression type:

```java
entityPath.getMap("map", String.class, String.class, StringPath.class).get("key").lower();
```

For `PathBuilder` validation, a `PathBuilderValidator` can be injected in the
constructor and will be used transitively for new `PathBuilder` instances:

```java
PathBuilder<Customer> customer = new PathBuilder<Customer>(Customer.class, "customer", validator);
```

`PathBuilderValidator.FIELDS` verifies field existence,
`PathBuilderValidator.PROPERTIES` validates Bean properties, and
`JPAPathBuilderValidator` validates using a JPA metamodel.

## Case Expressions

To construct case-when-then-else expressions, use the `CaseBuilder` class:

```java
QCustomer customer = QCustomer.customer;
Expression<String> cases = new CaseBuilder()
    .when(customer.annualSpending.gt(10000)).then("Premier")
    .when(customer.annualSpending.gt(5000)).then("Gold")
    .when(customer.annualSpending.gt(2000)).then("Silver")
    .otherwise("Bronze");
// The cases expression can now be used in a projection or condition
```

For case expressions with equals-operations, use the simpler form:

```java
QCustomer customer = QCustomer.customer;
Expression<String> cases = customer.annualSpending
    .when(10000).then("Premier")
    .when(5000).then("Gold")
    .when(2000).then("Silver")
    .otherwise("Bronze");
// The cases expression can now be used in a projection or condition
```

## Casting Expressions

To avoid a generic signature in expression types, the type hierarchies are
flattened. The result is that all generated query types are direct subclasses
of `com.querydsl.core.types.dsl.EntityPathBase` or
`com.querydsl.core.types.dsl.BeanPath` and cannot be directly cast to their
logical supertypes.

Instead of a direct Java cast, the supertype reference is accessible via the
`_super` field. A `_super` field is available in all generated query types with
a single supertype:

```java
// from Account
QAccount extends EntityPathBase<Account> {
    // ...
}

// from BankAccount extends Account
QBankAccount extends EntityPathBase<BankAccount> {

    public final QAccount _super = new QAccount(this);

    // ...
}
```

To cast from a supertype to a subtype, use the `as` method of the
`EntityPathBase` class:

```java
QAccount account = new QAccount("account");
QBankAccount bankAccount = account.as(QBankAccount.class);
```

## Select Literals

Literals can be selected by referring to them via constant expressions:

```java
query.select(Expressions.constant(1),
             Expressions.constant("abc"));
```

Constant expressions are often used in subqueries.
