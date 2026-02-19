---
layout: default
title: Result Handling
parent: Guides
nav_order: 2
---

# Result Handling

Querydsl provides two ways to customize results: `FactoryExpression` for
row-based transformation and `ResultTransformer` for aggregation.

The `com.querydsl.core.types.FactoryExpression` interface is used for bean
creation, constructor invocation, and for the creation of more complex objects.
The `FactoryExpression` implementations of Querydsl can be accessed via the
`com.querydsl.core.types.Projections` class.

For the `com.querydsl.core.ResultTransformer` interface, `GroupBy` is the main
implementation.

## Returning Multiple Columns

The default type for multi-column results is `com.querydsl.core.Tuple`. Tuple
provides a type-safe Map-like interface to access column data from a Tuple row
object.

```java
List<Tuple> result = query.select(employee.firstName, employee.lastName)
                          .from(employee).fetch();
for (Tuple row : result) {
     System.out.println("firstName " + row.get(employee.firstName));
     System.out.println("lastName " + row.get(employee.lastName));
}
```

This example could also have been written via the `QTuple` expression class:

```java
List<Tuple> result = query.select(new QTuple(employee.firstName, employee.lastName))
                          .from(employee).fetch();
for (Tuple row : result) {
     System.out.println("firstName " + row.get(employee.firstName));
     System.out.println("lastName " + row.get(employee.lastName));
}
```

## Bean Population

In cases where beans need to be populated based on the results of the query,
bean projections can be used:

```java
List<UserDTO> dtos = query.select(
    Projections.bean(UserDTO.class, user.firstName, user.lastName)).fetch();
```

When fields should be used directly instead of setters:

```java
List<UserDTO> dtos = query.select(
    Projections.fields(UserDTO.class, user.firstName, user.lastName)).fetch();
```

## Constructor Usage

Constructor-based row transformation:

```java
List<UserDTO> dtos = query.select(
    Projections.constructor(UserDTO.class, user.firstName, user.lastName)).fetch();
```

As an alternative to the generic constructor expression usage, constructors
can also be annotated with the `@QueryProjection` annotation:

```java
class CustomerDTO {

  @QueryProjection
  public CustomerDTO(long id, String name) {
     ...
  }

}
```

Then use it in the query:

```java
QCustomer customer = QCustomer.customer;
JPQLQuery query = new HibernateQuery(session);
List<CustomerDTO> dtos = query.select(new QCustomerDTO(customer.id, customer.name))
                              .from(customer).fetch();
```

While the example is Hibernate-specific, this feature is available in all
modules.

If the type with the `@QueryProjection` annotation is not an annotated entity
type, you can use the constructor projection like in the example. If the
annotated type is an entity type, the constructor projection needs to be
created via a call to the static `create` method of the query type:

```java
@Entity
class Customer {

  @QueryProjection
  public Customer(long id, String name) {
     ...
  }

}
```

```java
QCustomer customer = QCustomer.customer;
JPQLQuery query = new HibernateQuery(session);
List<Customer> dtos = query.select(QCustomer.create(customer.id, customer.name))
                           .from(customer).fetch();
```

Alternatively, if code generation is not an option:

```java
List<Customer> dtos = query
    .select(Projections.constructor(Customer.class, customer.id, customer.name))
    .from(customer).fetch();
```

## Result Aggregation

The `com.querydsl.core.group.GroupBy` class provides aggregation functionality
which can be used to aggregate query results in memory.

Aggregating parent-child relations:

```java
import static com.querydsl.core.group.GroupBy.*;

Map<Integer, List<Comment>> results = query.from(post, comment)
    .where(comment.post.id.eq(post.id))
    .transform(groupBy(post.id).as(list(comment)));
```

This returns a map of post ids to related comments.

Multiple result columns:

```java
Map<Integer, Group> results = query.from(post, comment)
    .where(comment.post.id.eq(post.id))
    .transform(groupBy(post.id).as(post.name, set(comment.id)));
```

This returns a map of post ids to `Group` instances with access to post name
and comment ids.

`Group` is the `GroupBy` equivalent to the `Tuple` interface.

More examples can be found in the
[GroupByTest](https://github.com/OpenFeign/querydsl/blob/master/querydsl-libraries/querydsl-collections/src/test/java/com/querydsl/collections/GroupByTest.java)
class.
