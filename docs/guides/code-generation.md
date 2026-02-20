---
layout: default
title: Code Generation
parent: Guides
nav_order: 3
---

# Code Generation

Querydsl uses Java annotation processing (APT) for code generation in the JPA
and MongoDB modules. This section describes various configuration options for
the code generation and alternatives to APT usage.

## Path Initialization

By default, Querydsl initializes only reference properties of the first two
levels. When longer initialization paths are required, annotate the domain
types with `com.querydsl.core.annotations.QueryInit`:

```java
@Entity
class Event {
    @QueryInit("customer.address")
    Account account;
}

@Entity
class Account {
    Customer customer;
}

@Entity
class Customer {
    String name;
    Address address;
    // ...
}
```

This enforces the initialization of the `account.customer` path when an
`Event` path is initialized as a root path / variable. The path initialization
format supports wildcards as well, e.g. `"customer.*"` or just `"*"`.

The automatic path initialization replaces the manual one, which required the
entity fields to be non-final. The declarative format has the benefit of being
applied to all top-level instances of a query type and enabling the usage of
final entity fields.

Automatic path initialization is the preferred initialization strategy, but
manual initialization can be activated via the `@Config` annotation.

## Customization

The serialization of Querydsl can be customized via `@Config` annotations on
packages and types.

### Config Options

| Name | Description |
|:-----|:------------|
| `entityAccessors` | Accessor methods for entity paths instead of public final fields (default: `false`) |
| `listAccessors` | `listProperty(int index)` style methods (default: `false`) |
| `mapAccessors` | `mapProperty(Key key)` style accessor methods (default: `false`) |
| `createDefaultVariable` | Generate the default variable (default: `true`) |
| `defaultVariableName` | Name of the default variable |

Examples:

Customization of entity type serialization:

```java
@Config(entityAccessors=true)
@Entity
public class User {
    //...
}
```

Customization of package content:

```java
@Config(listAccessors=true)
package com.querydsl.core.domain.rel;

import com.querydsl.core.annotations.Config;
```

### APT Options

To customize the serializer configuration globally, use the following APT
options:

| Name | Description |
|:-----|:------------|
| `querydsl.entityAccessors` | Enable reference field accessors |
| `querydsl.listAccessors` | Enable accessors for direct indexed list access |
| `querydsl.mapAccessors` | Enable accessors for direct key-based map access |
| `querydsl.prefix` | Override the prefix for query types (default: `Q`) |
| `querydsl.suffix` | Set a suffix for query types |
| `querydsl.packageSuffix` | Set a suffix for query type packages |
| `querydsl.createDefaultVariable` | Set whether default variables are created |
| `querydsl.unknownAsEmbeddable` | Set whether unknown non-annotated classes should be treated as embeddable (default: `false`) |
| `querydsl.includedPackages` | Comma-separated list of packages to include into code generation (default: all) |
| `querydsl.includedClasses` | Comma-separated list of class names to include into code generation (default: all) |
| `querydsl.excludedPackages` | Comma-separated list of packages to exclude from code generation (default: none) |
| `querydsl.excludedClasses` | Comma-separated list of class names to exclude from code generation (default: none) |
| `querydsl.useFields` | Set whether fields are used as metadata source (default: `true`) |
| `querydsl.useGetters` | Set whether accessors are used as metadata source (default: `true`) |
| `querydsl.generatedAnnotationClass` | Fully qualified class name of the annotation to add on generated sources |

### Using maven-compiler-plugin

The recommended approach is to configure `maven-compiler-plugin` to hook APT
directly into compilation:

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <generatedSourcesDirectory>target/generated-sources/java</generatedSourcesDirectory>
    <compilerArgs>
      <arg>-Aquerydsl.entityAccessors=true</arg>
      <arg>-Aquerydsl.useFields=false</arg>
    </compilerArgs>
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

You need to use a proper classifier when defining the dependency on
`{{ site.group_id }}:querydsl-apt`. The additional artifacts define the
annotation processor to be used in
`META-INF/services/javax.annotation.processing.Processor`.

Available classifiers:

- `general`
- `hibernate`
- `jpa`

With this configuration, query objects have their sources generated and
compiled during compilation of the domain objects. This also automatically
adds the generated sources directory to Maven project source roots.

## Custom Type Mappings

Custom type mappings can be used on properties to override the derived `Path`
type. This is useful in cases where comparison and String operations should be
blocked on certain String paths, or Date/Time support for custom types needs
to be added.

Support for Date/Time types of the Joda time API and JDK (`java.util.Date`,
`Calendar` and subtypes) is built in, but other APIs might need to be
supported using this feature.

```java
@Entity
public class MyEntity {
    @QueryType(PropertyType.SIMPLE)
    public String stringAsSimple;

    @QueryType(PropertyType.COMPARABLE)
    public String stringAsComparable;

    @QueryType(PropertyType.NONE)
    public String stringNotInQuerydsl;
}
```

The value `PropertyType.NONE` can be used to skip a property in query type
generation. This is different from `@Transient` or `@QueryTransient`
annotated properties, where properties are not persisted. `PropertyType.NONE`
just omits the property from the Querydsl query type.

## Delegate Methods

To declare a static method as a delegate method, add the `@QueryDelegate`
annotation with the corresponding domain type as a value and provide a method
signature that takes the corresponding Querydsl query type as the first
argument.

```java
@QueryEntity
public static class User {
    String name;
    User manager;
}
```

```java
@QueryDelegate(User.class)
public static BooleanPath isManagedBy(QUser user, User other) {
    return user.manager.eq(other);
}
```

The generated method in the `QUser` query type:

```java
public BooleanPath isManagedBy(QUser other) {
    return DelegateTest.isManagedBy(this, other);
}
```

Delegate methods can also extend built-in types:

```java
public class QueryExtensions {

    @QueryDelegate(Date.class)
    public static BooleanExpression inPeriod(DatePath<Date> date, Pair<Date,Date> period) {
        return date.goe(period.getFirst()).and(date.loe(period.getSecond()));
    }

    @QueryDelegate(Timestamp.class)
    public static BooleanExpression inDatePeriod(DateTimePath<Timestamp> timestamp, Pair<Date,Date> period) {
        Timestamp first = new Timestamp(DateUtils.truncate(period.getFirst(), Calendar.DAY_OF_MONTH).getTime());
        Calendar second = Calendar.getInstance();
        second.setTime(DateUtils.truncate(period.getSecond(), Calendar.DAY_OF_MONTH));
        second.add(1, Calendar.DAY_OF_MONTH);
        return timestamp.goe(first).and(timestamp.lt(new Timestamp(second.getTimeInMillis())));
    }
}
```

When delegate methods are declared for built-in types, subclasses with the
proper delegate method usages are created.

## Non-annotated Types

It is possible to create Querydsl query types for non-annotated types by
creating `@QueryEntities` annotations. Place a `@QueryEntities` annotation
into a package of your choice and the classes to mirror in the value attribute.

To create the types, use the `com.querydsl.apt.QuerydslAnnotationProcessor`.
In Maven, configure the `maven-compiler-plugin`:

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
      <classifier>general</classifier>
    </dependency>
  </dependencies>
</plugin>
```

## Classpath-based Code Generation

For cases where annotated Java sources are not available, such as the usage of
a different JVM language (Scala, Groovy) or annotation addition via bytecode
manipulation, the `GenericExporter` class can be used to scan the classpath
for annotated classes and generate query types.

Add a dependency to the `querydsl-codegen` module:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-codegen</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

Example for JPA:

```java
GenericExporter exporter = new GenericExporter();
exporter.setKeywords(Keywords.JPA);
exporter.setEntityAnnotation(Entity.class);
exporter.setEmbeddableAnnotation(Embeddable.class);
exporter.setEmbeddedAnnotation(Embedded.class);
exporter.setSupertypeAnnotation(MappedSuperclass.class);
exporter.setSkipAnnotation(Transient.class);
exporter.setTargetFolder(new File("target/generated-sources/java"));
exporter.export(DomainClass.class.getPackage());
```

This exports all JPA-annotated classes in the package of `DomainClass` and
subpackages to the `target/generated-sources/java` directory.

### Usage via Maven

The goals `generic-export`, `jpa-export`, and `jdo-export` of the
`querydsl-maven-plugin` can be used for `GenericExporter` usage via Maven.

| Type | Element | Description |
|:-----|:--------|:------------|
| `File` | `targetFolder` | Target folder for generated sources |
| `boolean` | `scala` | `true` if Scala sources should be generated instead (default: `false`) |
| `String[]` | `packages` | Packages to be introspected for entity classes |
| `boolean` | `handleFields` | `true` if fields should be treated as properties (default: `true`) |
| `boolean` | `handleMethods` | `true` if getters should be treated as properties (default: `true`) |
| `String` | `sourceEncoding` | Charset encoding for generated source files |
| `boolean` | `testClasspath` | `true` if the test classpath should be used instead |

Example for JPA annotated classes:

```xml
<plugin>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-maven-plugin</artifactId>
  <version>{{ site.querydsl_version }}</version>
  <executions>
    <execution>
      <phase>process-classes</phase>
      <goals>
        <goal>jpa-export</goal>
      </goals>
      <configuration>
        <targetFolder>target/generated-sources/java</targetFolder>
        <packages>
          <package>com.example.domain</package>
        </packages>
      </configuration>
    </execution>
  </executions>
</plugin>
```

This exports the JPA-annotated classes of the `com.example.domain` package and
subpackages to the `target/generated-sources/java` directory.

If you need to compile the generated sources directly after that, use the
`compile` goal:

```xml
<execution>
  <goals>
    <goal>compile</goal>
  </goals>
  <configuration>
    <sourceFolder>target/generated-sources/java</sourceFolder>
  </configuration>
</execution>
```

### Scala Support

For Scala output, use a variant of the following configuration:

```xml
<plugin>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-maven-plugin</artifactId>
  <version>{{ site.querydsl_version }}</version>
  <dependencies>
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
  <executions>
    <execution>
      <goals>
        <goal>jpa-export</goal>
      </goals>
      <configuration>
        <targetFolder>target/generated-sources/scala</targetFolder>
        <scala>true</scala>
        <packages>
          <package>com.example.domain</package>
        </packages>
      </configuration>
    </execution>
  </executions>
</plugin>
```
