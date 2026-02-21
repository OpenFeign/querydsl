---
layout: default
title: Querying JPA
parent: Tutorials
nav_order: 1
---

# Querying JPA

Querydsl defines a general statically typed syntax for querying on top of
persisted domain model data. This guide describes how to use Querydsl in
combination with JPA.

Querydsl for JPA is an alternative to both JPQL and Criteria queries. It
combines the dynamic nature of Criteria queries with the expressiveness of
JPQL — all in a fully type-safe manner.

## Maven Integration

Add the following dependencies to your Maven project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

Configure the `maven-compiler-plugin` to run the Querydsl annotation processor
during compilation:

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <generatedSourcesDirectory>target/generated-sources/java</generatedSourcesDirectory>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>{{ site.group_id }}</groupId>
      <artifactId>querydsl-apt</artifactId>
      <version>{{ site.querydsl_version }}</version>
      <classifier>jpa</classifier>
    </dependency>
    <dependency>
      <groupId>jakarta.persistence</groupId>
      <artifactId>jakarta.persistence-api</artifactId>
      <version>3.1.0</version>
    </dependency>
  </dependencies>
</plugin>
```

The `JPAAnnotationProcessor` finds domain types annotated with the
`jakarta.persistence.Entity` annotation and generates query types for them.

If you use Hibernate annotations in your domain types, use the processor
`com.querydsl.apt.hibernate.HibernateAnnotationProcessor` instead. See the
[Hibernate tutorial]({{ site.baseurl }}/tutorials/hibernate) for
Hibernate-specific features such as Common Table Expressions, query caching,
and read-only mode.

Run `mvn clean install` and your query types will be generated into
`target/generated-sources/java`.

## Generating the Model from hbm.xml Files

If you use Hibernate with an XML-based configuration, you can use the XML
metadata to create your Querydsl model.

`com.querydsl.jpa.codegen.HibernateDomainExporter` provides this
functionality:

```java
HibernateDomainExporter exporter = new HibernateDomainExporter(
  "Q",                     // name prefix
  new File("target/gen3"), // target folder
  configuration);          // instance of org.hibernate.cfg.Configuration

exporter.export();
```

The `HibernateDomainExporter` must be executed within a classpath where the
domain types are visible, since property types are resolved via reflection.

All JPA annotations are ignored, but Querydsl annotations such as `@QueryInit`
and `@QueryType` are taken into account.

## Using Query Types

To create queries with Querydsl you need to instantiate variables and query
implementations.

Assume that your project has the following domain type:

```java
@Entity
public class Customer {
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String fn) {
        firstName = fn;
    }

    public void setLastName(String ln) {
        lastName = ln;
    }
}
```

Querydsl generates a query type with the simple name `QCustomer` into the same
package as `Customer`. `QCustomer` can be used as a statically typed variable
in Querydsl queries as a representative for the `Customer` type.

`QCustomer` has a default instance variable accessible as a static field:

```java
QCustomer customer = QCustomer.customer;
```

Alternatively, define your own variable:

```java
QCustomer customer = new QCustomer("myCustomer");
```

## Querying

The Querydsl JPA module supports both the JPA and the Hibernate API.

To use the JPA API, create `JPAQuery` instances like this:

```java
// where entityManager is a JPA EntityManager
JPAQuery<?> query = new JPAQuery<Void>(entityManager);
```

If you use the Hibernate API instead, instantiate a `HibernateQuery`:

```java
// where session is a Hibernate session
HibernateQuery<?> query = new HibernateQuery<Void>(session);
```

Both `JPAQuery` and `HibernateQuery` implement the `JPQLQuery` interface.

For the examples in this chapter, queries are created via a `JPAQueryFactory`
instance. `JPAQueryFactory` should be the preferred option for obtaining
`JPAQuery` instances. For the Hibernate API, `HibernateQueryFactory` can be
used. For Hibernate-specific features such as CTEs, query caching, and
read-only mode, see the
[Hibernate tutorial]({{ site.baseurl }}/tutorials/hibernate).

To retrieve the customer with the first name Bob:

```java
QCustomer customer = QCustomer.customer;
Customer bob = queryFactory.selectFrom(customer)
  .where(customer.firstName.eq("Bob"))
  .fetchOne();
```

The `selectFrom` call defines the query source and projection, the `where`
part defines the filter, and `fetchOne` tells Querydsl to return a single
element.

To create a query with multiple sources:

```java
QCustomer customer = QCustomer.customer;
QCompany company = QCompany.company;
query.from(customer, company);
```

To use multiple filters:

```java
queryFactory.selectFrom(customer)
    .where(customer.firstName.eq("Bob"), customer.lastName.eq("Wilson"));
```

Or equivalently:

```java
queryFactory.selectFrom(customer)
    .where(customer.firstName.eq("Bob").and(customer.lastName.eq("Wilson")));
```

In native JPQL form the query would be:

```
select customer from Customer as customer
where customer.firstName = "Bob" and customer.lastName = "Wilson"
```

To combine the filters via "or":

```java
queryFactory.selectFrom(customer)
    .where(customer.firstName.eq("Bob").or(customer.lastName.eq("Wilson")));
```

## Using Joins

Querydsl supports the following join variants in JPQL: inner join, join, left
join, and right join. Join usage is type-safe and follows this pattern:

```java
QCat cat = QCat.cat;
QCat mate = new QCat("mate");
QCat kitten = new QCat("kitten");
queryFactory.selectFrom(cat)
    .innerJoin(cat.mate, mate)
    .leftJoin(cat.kittens, kitten)
    .fetch();
```

The native JPQL version:

```
select cat from Cat as cat
inner join cat.mate as mate
left outer join cat.kittens as kitten
```

Another example:

```java
queryFactory.selectFrom(cat)
    .leftJoin(cat.kittens, kitten)
    .on(kitten.bodyWeight.gt(10.0))
    .fetch();
```

## General Usage

Use the cascading methods of the `JPQLQuery` interface:

- **select:** Set the projection of the query. (Not necessary if created via
  query factory)
- **from:** Add query sources.
- **innerJoin, join, leftJoin, rightJoin, on:** Add join elements. For join
  methods, the first argument is the join source and the second the target
  (alias).
- **where:** Add query filters, either in varargs form separated via commas or
  cascaded via the `and` operator.
- **groupBy:** Add group by arguments in varargs form.
- **having:** Add having filters of the "group by" grouping as a varargs array
  of Predicate expressions.
- **orderBy:** Add ordering of the result as a varargs array of order
  expressions. Use `asc()` and `desc()` on numeric, string, and other
  comparable expressions to access `OrderSpecifier` instances.
- **limit, offset, restrict:** Set the paging of the result. `limit` for max
  results, `offset` for skipping rows, and `restrict` for defining both in one
  call.

## Ordering

```java
QCustomer customer = QCustomer.customer;
queryFactory.selectFrom(customer)
    .orderBy(customer.lastName.asc(), customer.firstName.desc())
    .fetch();
```

Equivalent native JPQL:

```
select customer from Customer as customer
order by customer.lastName asc, customer.firstName desc
```

## Grouping

```java
queryFactory.select(customer.lastName).from(customer)
    .groupBy(customer.lastName)
    .fetch();
```

Equivalent native JPQL:

```
select customer.lastName
from Customer as customer
group by customer.lastName
```

## Delete Clauses

Delete clauses follow a simple delete-where-execute form:

```java
QCustomer customer = QCustomer.customer;
// delete all customers
queryFactory.delete(customer).execute();
// delete all customers with a level less than 3
queryFactory.delete(customer).where(customer.level.lt(3)).execute();
```

The `where` call is optional and `execute` performs the deletion and returns
the number of deleted entities.

DML clauses in JPA do not take JPA-level cascade rules into account and do not
provide fine-grained second-level cache interaction.

## Update Clauses

Update clauses follow a simple update-set/where-execute form:

```java
QCustomer customer = QCustomer.customer;
// rename customers named Bob to Bobby
queryFactory.update(customer).where(customer.name.eq("Bob"))
    .set(customer.name, "Bobby")
    .execute();
```

The `set` invocations define the property updates in SQL-Update style and
`execute` performs the update and returns the number of updated entities.

DML clauses in JPA do not take JPA-level cascade rules into account and do not
provide fine-grained second-level cache interaction.

## Subqueries

To create a subquery, use the static factory methods of `JPAExpressions` and
define the query parameters via `from`, `where`, etc.

```java
QDepartment department = QDepartment.department;
QDepartment d = new QDepartment("d");
queryFactory.selectFrom(department)
    .where(department.size.eq(
        JPAExpressions.select(d.size.max()).from(d)))
     .fetch();
```

Another example:

```java
QEmployee employee = QEmployee.employee;
QEmployee e = new QEmployee("e");
queryFactory.selectFrom(employee)
    .where(employee.weeklyhours.gt(
        JPAExpressions.select(e.weeklyhours.avg())
            .from(employee.department.employees, e)
            .where(e.manager.eq(employee.manager))))
    .fetch();
```

## Exposing the Original Query

If you need to tune the original JPA `Query` before execution, you can expose
it:

```java
Query jpaQuery = queryFactory.selectFrom(employee).createQuery();
// ...
List results = jpaQuery.getResultList();
```

## Using Native SQL in JPA Queries

Querydsl supports Native SQL in JPA via the `JPASQLQuery` class.

To use it, you must generate Querydsl query types for your SQL schema. This
can be done with the following Maven configuration:

```xml
<plugin>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-maven-plugin</artifactId>
  <version>{{ site.querydsl_version }}</version>
  <executions>
    <execution>
      <goals>
        <goal>export</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <jdbcDriver>org.apache.derby.jdbc.EmbeddedDriver</jdbcDriver>
    <jdbcUrl>jdbc:derby:target/demoDB;create=true</jdbcUrl>
    <packageName>com.mycompany.mydomain</packageName>
    <targetFolder>${project.basedir}/target/generated-sources/java</targetFolder>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>org.apache.derby</groupId>
      <artifactId>derby</artifactId>
      <version>${derby.version}</version>
    </dependency>
  </dependencies>
</plugin>
```

When the query types have been generated, you can use them in your queries.

Single column query:

```java
// serialization templates
SQLTemplates templates = new DerbyTemplates();
// query types (S* for SQL, Q* for domain types)
SAnimal cat = new SAnimal("cat");
SAnimal mate = new SAnimal("mate");
QCat catEntity = QCat.cat;

JPASQLQuery<?> query = new JPASQLQuery<Void>(entityManager, templates);
List<String> names = query.select(cat.name).from(cat).fetch();
```

If you mix entity (e.g. `QCat`) and table (e.g. `SAnimal`) references in your
query, make sure they use the same variable names. `SAnimal.animal` has the
variable name "animal", so a new instance (`new SAnimal("cat")`) was used
instead.

An alternative pattern:

```java
QCat catEntity = QCat.cat;
SAnimal cat = new SAnimal(catEntity.getMetadata().getName());
```

Query multiple columns:

```java
query = new JPASQLQuery<Void>(entityManager, templates);
List<Tuple> rows = query.select(cat.id, cat.name).from(cat).fetch();
```

Query all columns:

```java
List<Tuple> rows = query.select(cat.all()).from(cat).fetch();
```

Query in SQL, but project as entity:

```java
query = new JPASQLQuery<Void>(entityManager, templates);
List<Cat> cats = query.select(catEntity).from(cat).orderBy(cat.name.asc()).fetch();
```

Query with joins:

```java
query = new JPASQLQuery<Void>(entityManager, templates);
cats = query.select(catEntity).from(cat)
    .innerJoin(mate).on(cat.mateId.eq(mate.id))
    .where(cat.dtype.eq("Cat"), mate.dtype.eq("Cat"))
    .fetch();
```

Query and project into DTO:

```java
query = new JPASQLQuery<Void>(entityManager, templates);
List<CatDTO> catDTOs = query.select(Projections.constructor(CatDTO.class, cat.id, cat.name))
    .from(cat)
    .orderBy(cat.name.asc())
    .fetch();
```

If you use the Hibernate API instead of the JPA API, use `HibernateSQLQuery`
instead. See the
[Hibernate tutorial]({{ site.baseurl }}/tutorials/hibernate) for more
details on `HibernateSQLQuery` and other Hibernate-specific features.
