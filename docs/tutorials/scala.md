---
layout: default
title: Querying in Scala
parent: Tutorials
nav_order: 9
---

# Querying in Scala

Generic support for Querydsl usage in Scala is available via the
`querydsl-scala` module.

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-scala</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

## DSL Expressions for Scala

Querydsl for Scala provides an alternative DSL for expression construction.
The Scala DSL utilizes language features such as operator overloading, function
pointers, and implicit imports for enhanced readability and conciseness.

Here is an overview of the main alternatives:

```
Standard              Alternative

expr isNotNull        expr is not(null)
expr isNull           expr is null
expr eq "Ben"         expr === "Ben"
expr ne "Ben"         expr !== "Ben"
expr append "X"       expr + "X"
expr isEmpty          expr is empty
expr isNotEmpty       expr not empty

// boolean
left and right        left && right
left or right         left || right
expr not              !expr

// comparison
expr lt 5             expr < 5
expr loe 5            expr <= 5
expr gt 5             expr > 5
expr goe 5            expr >= 5
expr notBetween(2,6)  expr not between (2,6)
expr negate           -expr

// numeric
expr add 3            expr + 3
expr subtract 3       expr - 3
expr divide 3         expr / 3
expr multiply 3       expr * 3
expr mod 5            expr % 5

// collection
list.get(0)           list(0)
map.get("X")          map("X")
```

## Querying with SQL

Like with Querydsl SQL for Java, you need to generate query types to construct
queries.

Generation without bean types:

```scala
val directory = new java.io.File("target/jdbcgen1")
val namingStrategy = new DefaultNamingStrategy()
val exporter = new MetaDataExporter()
exporter.setNamePrefix("Q")
exporter.setPackageName("com.querydsl")
exporter.setSchemaPattern("PUBLIC")
exporter.setTargetFolder(directory)
exporter.setSerializerClass(classOf[ScalaMetaDataSerializer])
exporter.setCreateScalaSources(true)
exporter.setTypeMappings(ScalaTypeMappings.create)
exporter.export(connection.getMetaData)
```

Generation with bean types:

```scala
val directory = new java.io.File("target/jdbcgen2")
val namingStrategy = new DefaultNamingStrategy()
val exporter = new MetaDataExporter()
exporter.setNamePrefix("Q")
exporter.setPackageName("com.querydsl")
exporter.setSchemaPattern("PUBLIC")
exporter.setTargetFolder(directory)
exporter.setSerializerClass(classOf[ScalaMetaDataSerializer])
exporter.setBeanSerializerClass(classOf[ScalaBeanSerializer])
exporter.setCreateScalaSources(true)
exporter.setTypeMappings(ScalaTypeMappings.create)
exporter.export(connection.getMetaData)
```

### Code Generation via Maven

Scala sources for SQL metatypes and projections can be generated with the
`querydsl-maven-plugin`:

```xml
<plugin>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-maven-plugin</artifactId>
  <version>{{ site.querydsl_version }}</version>
  <configuration>
    <jdbcDriver>com.mysql.jdbc.Driver</jdbcDriver>
    <jdbcUrl>jdbc:mysql://localhost:3306/test</jdbcUrl>
    <jdbcUser>matko</jdbcUser>
    <jdbcPassword>matko</jdbcPassword>
    <packageName>com.example.schema</packageName>
    <targetFolder>${project.basedir}/src/main/scala</targetFolder>
    <exportBeans>true</exportBeans>
    <createScalaSources>true</createScalaSources>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.16</version>
    </dependency>
    <dependency>
      <groupId>{{ site.group_id }}</groupId>
      <artifactId>querydsl-scala</artifactId>
      <version>{{ site.querydsl_version }}</version>
    </dependency>
    <dependency>
      <groupId>org.scala-lang</groupId>
      <artifactId>scala-library</artifactId>
      <version>${scala.version}</version>
    </dependency>
  </dependencies>
</plugin>
```

The Maven goal to execute is `querydsl:export`.

## Querying with Other Backends

When querying with other backends the expression model has to be created
manually or alternatively the alias functionality can be used.

Here is a minimal example with JPA:

```scala
@Entity
class User {
  @BeanProperty
  @Id
  var id: Integer = _
  @BeanProperty
  var userName: String = _
  @BeanProperty
  @ManyToOne
  var department: Department = _
}

@Entity
class Department {
  @BeanProperty
  @Id
  var id: Integer = _
  @BeanProperty
  var name: String = _
}
```

Query examples:

```scala
val person = Person as "person"

// List
selectFrom(person).where(person.firstName like "Rob%").fetch()

// Unique result
selectFrom(person).where(person.firstName like "Rob%").fetchOne()

// Long where
selectFrom(person)
  .where(person.firstName like "Rob%", person.lastName like "An%")
  .fetch()

// Order
selectFrom(person).orderBy(person.firstName asc).fetch()

// Not null
selectFrom(person)
  .where(person.firstName isEmpty, person.lastName isNotNull)
  .fetch()
```

The factory method for query creation is:

```scala
def query() = new JPAQuery(entityManager)
```

Variables can be created like this:

```scala
val person = Person as "person"
```

Scala support is not yet available when using Hibernate with an XML-based
configuration. `HibernateDomainExporter` currently only outputs Java source
files.
