---
layout: default
title: Querying SQL
parent: Tutorials
nav_order: 2
---

# Querying SQL

This chapter describes the query type generation and querying functionality of
the SQL module.

## Maven Integration

Add the following dependencies to your Maven project:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-sql</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>

<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-sql-codegen</artifactId>
  <version>{{ site.querydsl_version }}</version>
  <scope>provided</scope>
</dependency>
```

The `querydsl-sql-codegen` dependency can be skipped if code generation happens
via Maven.

## Code Generation via Maven

This functionality should be primarily used via the Maven plugin:

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
    <packageName>com.myproject.domain</packageName>
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

Use the goal `test-export` to treat the target folder as a test source folder.

### Plugin Parameters

| Name | Description |
|:-----|:------------|
| `jdbcDriver` | Class name of the JDBC driver |
| `jdbcUrl` | JDBC URL |
| `jdbcUser` | JDBC user |
| `jdbcPassword` | JDBC password |
| `namePrefix` | Name prefix for generated query classes (default: `Q`) |
| `nameSuffix` | Name suffix for generated query classes (default: empty) |
| `beanPrefix` | Name prefix for generated bean classes |
| `beanSuffix` | Name suffix for generated bean classes |
| `packageName` | Package name where source files should be generated |
| `beanPackageName` | Package name where bean files should be generated (default: `packageName`) |
| `beanInterfaces` | Array of interface class names to add to the bean classes (default: empty) |
| `beanAddToString` | Set to `true` to create a default `toString()` implementation (default: `false`) |
| `beanAddFullConstructor` | Set to `true` to create a full constructor in addition to the public empty constructor (default: `false`) |
| `beanPrintSupertype` | Set to `true` to print the supertype as well (default: `false`) |
| `schemaPattern` | Schema name pattern in LIKE form; multiple can be separated by comma (default: `null`) |
| `tableNamePattern` | Table name pattern in LIKE form; multiple can be separated by comma (default: `null`) |
| `targetFolder` | Target folder where sources should be generated |
| `beansTargetFolder` | Target folder where bean sources should be generated (defaults to `targetFolder`) |
| `namingStrategyClass` | Class name of the `NamingStrategy` (default: `DefaultNamingStrategy`) |
| `beanSerializerClass` | Class name of the `BeanSerializer` (default: `BeanSerializer`) |
| `serializerClass` | Class name of the `Serializer` (default: `MetaDataSerializer`) |
| `exportBeans` | Set to `true` to generate beans as well (default: `false`) |
| `innerClassesForKeys` | Set to `true` to generate inner classes for keys (default: `false`) |
| `validationAnnotations` | Set to `true` to enable serialization of validation annotations (default: `false`) |
| `columnAnnotations` | Export column annotations (default: `false`) |
| `createScalaSources` | Whether to export Scala sources instead of Java sources (default: `false`) |
| `schemaToPackage` | Append schema name to package (default: `false`) |
| `lowerCase` | Lower case transformation of names (default: `false`) |
| `exportTables` | Export tables (default: `true`) |
| `exportViews` | Export views (default: `true`) |
| `exportPrimaryKeys` | Export primary keys (default: `true`) |
| `tableTypesToExport` | Comma-separated list of table types to export. Overrides `exportTables` and `exportViews` if set. |
| `exportForeignKeys` | Export foreign keys (default: `true`) |
| `exportDirectForeignKeys` | Export direct foreign keys (default: `true`) |
| `exportInverseForeignKeys` | Export inverse foreign keys (default: `true`) |
| `customTypes` | Custom user types (default: none) |
| `typeMappings` | Mappings of `table.column` to Java type (default: none) |
| `numericMappings` | Mappings of size/digits to Java type (default: none) |
| `imports` | Array of Java imports added to generated query classes (default: empty) |
| `generatedAnnotationClass` | Fully qualified class name of the annotation to add on generated sources |

Custom types can be used to register additional Type implementations:

```xml
<customTypes>
  <customType>com.querydsl.sql.types.InputStreamType</customType>
</customTypes>
```

Type mappings can be used to register table.column specific Java types:

```xml
<typeMappings>
  <typeMapping>
    <table>IMAGE</table>
    <column>CONTENTS</column>
    <type>java.io.InputStream</type>
  </typeMapping>
</typeMappings>
```

### Default Numeric Mappings

| Total digits | Decimal digits | Type |
|:-------------|:---------------|:-----|
| > 18 | 0 | `BigInteger` |
| > 9 | 0 | `Long` |
| > 4 | 0 | `Integer` |
| > 2 | 0 | `Short` |
| > 0 | 0 | `Byte` |
| > 0 | > 0 | `BigDecimal` |

Customized numeric mappings:

```xml
<numericMappings>
  <numericMapping>
    <total>1</total>
    <decimal>0</decimal>
    <javaType>java.lang.Byte</javaType>
  </numericMapping>
</numericMappings>
```

### Rename Mappings

Schemas, tables, and columns can be renamed using the plugin:

```xml
<renameMappings>
  <renameMapping>
    <fromSchema>PROD</fromSchema>
    <toSchema>TEST</toSchema>
  </renameMapping>
</renameMappings>
```

Renaming a table:

```xml
<renameMappings>
  <renameMapping>
    <fromSchema>PROD</fromSchema>
    <fromTable>CUSTOMER</fromTable>
    <toTable>CSTMR</toTable>
  </renameMapping>
</renameMappings>
```

Renaming a column:

```xml
<renameMappings>
  <renameMapping>
    <fromSchema>PROD</fromSchema>
    <fromTable>CUSTOMER</fromTable>
    <fromColumn>ID</fromColumn>
    <toColumn>IDX</toColumn>
  </renameMapping>
</renameMappings>
```

`fromSchema` can be omitted when renaming tables and columns.

## Creating the Query Types

To get started, export your schema into Querydsl query types:

```java
java.sql.Connection conn = ...;
MetaDataExporter exporter = new MetaDataExporter();
exporter.setPackageName("com.myproject.mydomain");
exporter.setTargetFolder(new File("target/generated-sources/java"));
exporter.export(conn.getMetaData());
```

This declares that the database schema is to be mirrored into the
`com.myproject.mydomain` package in the `target/generated-sources/java` folder.

The generated types have the table name transformed to mixed case as the class
name and a similar mixed case transformation applied to the columns which are
available as property paths in the query type.

In addition, primary key and foreign key constraints are provided as fields
which can be used for compact join declarations.

## Configuration

The configuration is done via `com.querydsl.sql.Configuration` which takes a
Querydsl SQL dialect as an argument. For H2:

```java
SQLTemplates templates = new H2Templates();
Configuration configuration = new Configuration(templates);
```

Querydsl uses SQL dialects to customize the SQL serialization needed for
different relational databases. The available dialects are:

- `CUBRIDTemplates`
- `DB2Templates`
- `DerbyTemplates`
- `FirebirdTemplates`
- `HSQLDBTemplates`
- `H2Templates`
- `MySQLTemplates`
- `OracleTemplates`
- `PostgreSQLTemplates`
- `SQLiteTemplates`
- `SQLServerTemplates`
- `SQLServer2005Templates`
- `SQLServer2008Templates`
- `SQLServer2012Templates`
- `TeradataTemplates`

For customized `SQLTemplates` instances, use the builder pattern:

```java
H2Templates.builder()
   .printSchema()              // include the schema in the output
   .quote()                    // quote names
   .newLineToSingleSpace()     // replace new lines with single space
   .escape(ch)                 // set the escape char
   .build();                   // get the customized SQLTemplates instance
```

The methods of the `Configuration` class can be used to enable direct
serialization of literals via `setUseLiterals(true)`, override schema and
tables, and register custom types. See the Javadocs for full details.

## Querying

For the following examples we use the `SQLQueryFactory` class for query
creation:

```java
SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, dataSource);
```

Querying with Querydsl SQL:

```java
QCustomer customer = new QCustomer("c");

List<String> lastNames = queryFactory.select(customer.lastName).from(customer)
    .where(customer.firstName.eq("Bob"))
    .fetch();
```

This is transformed into the following SQL, assuming the related table name is
`customer` and the columns `first_name` and `last_name`:

```sql
SELECT c.last_name
FROM customer c
WHERE c.first_name = 'Bob'
```

## General Usage

Use the cascading methods of the `SQLQuery` class:

- **select:** Set the projection of the query. (Not necessary if created via
  query factory)
- **from:** Add query sources.
- **innerJoin, join, leftJoin, rightJoin, fullJoin, on:** Add join elements.
  For join methods, the first argument is the join source and the second the
  target (alias).
- **where:** Add query filters, either in varargs form separated via commas or
  cascaded via the `and` operator.
- **groupBy:** Add group by arguments in varargs form.
- **having:** Add having filter of the "group by" grouping as a varargs array
  of Predicate expressions.
- **orderBy:** Add ordering of the result as a varargs array of order
  expressions. Use `asc()` and `desc()` on numeric, string, and other
  comparable expressions to access `OrderSpecifier` instances.
- **limit, offset, restrict:** Set the paging of the result. `limit` for max
  results, `offset` for skipping rows, and `restrict` for defining both in one
  call.

## Joins

Joins are constructed using the following syntax:

```java
QCustomer customer = QCustomer.customer;
QCompany company = QCompany.company;
queryFactory.select(customer.firstName, customer.lastName, company.name)
    .from(customer)
    .innerJoin(customer.company, company)
    .fetch();
```

For a left join:

```java
queryFactory.select(customer.firstName, customer.lastName, company.name)
    .from(customer)
    .leftJoin(customer.company, company)
    .fetch();
```

Alternatively, the join condition can be written out:

```java
queryFactory.select(customer.firstName, customer.lastName, company.name)
    .from(customer)
    .leftJoin(company).on(customer.company.eq(company))
    .fetch();
```

## Ordering

```java
queryFactory.select(customer.firstName, customer.lastName)
    .from(customer)
    .orderBy(customer.lastName.asc(), customer.firstName.asc())
    .fetch();
```

Equivalent SQL:

```sql
SELECT c.first_name, c.last_name
FROM customer c
ORDER BY c.last_name ASC, c.first_name ASC
```

## Grouping

```java
queryFactory.select(customer.lastName)
    .from(customer)
    .groupBy(customer.lastName)
    .fetch();
```

Equivalent SQL:

```sql
SELECT c.last_name
FROM customer c
GROUP BY c.last_name
```

## Using Subqueries

To create a subquery, use one of the factory methods of `SQLExpressions` and
add the query parameters via `from`, `where`, etc.

```java
QCustomer customer = QCustomer.customer;
QCustomer customer2 = new QCustomer("customer2");
queryFactory.select(customer.all())
    .from(customer)
    .where(customer.status.eq(
        SQLExpressions.select(customer2.status.max()).from(customer2)))
    .fetch();
```

Another example:

```java
QStatus status = QStatus.status;
queryFactory.select(customer.all())
    .from(customer)
    .where(customer.status.in(
        SQLExpressions.select(status.id).from(status).where(status.level.lt(3))))
    .fetch();
```

## Selecting Literals

To select literals, create constant instances:

```java
queryFactory.select(Expressions.constant(1),
                    Expressions.constant("abc"));
```

The class `com.querydsl.core.types.dsl.Expressions` also offers other useful
static methods for projections, operations, and template creation.

## Query Extension Support

Custom query extensions to support engine-specific syntax can be created by
subclassing `AbstractSQLQuery` and adding flagging methods like in this
`MySQLQuery` example:

```java
public class MySQLQuery<T> extends AbstractSQLQuery<T, MySQLQuery<T>> {

    public MySQLQuery(Connection conn) {
        this(conn, new MySQLTemplates(), new DefaultQueryMetadata());
    }

    public MySQLQuery(Connection conn, SQLTemplates templates) {
        this(conn, templates, new DefaultQueryMetadata());
    }

    protected MySQLQuery(Connection conn, SQLTemplates templates, QueryMetadata metadata) {
        super(conn, new Configuration(templates), metadata);
    }

    public MySQLQuery bigResult() {
        return addFlag(Position.AFTER_SELECT, "SQL_BIG_RESULT ");
    }

    public MySQLQuery bufferResult() {
        return addFlag(Position.AFTER_SELECT, "SQL_BUFFER_RESULT ");
    }

    // ...
}
```

The flags are custom SQL snippets that can be inserted at specific points in
the serialization. The supported positions are the enums of the
`com.querydsl.core.QueryFlag.Position` enum class.

## Window Functions

Window functions are supported via the methods in the `SQLExpressions` class:

```java
queryFactory.select(SQLExpressions.rowNumber()
        .over()
        .partitionBy(employee.name)
        .orderBy(employee.id))
     .from(employee)
```

## Common Table Expressions

Common table expressions are supported via two syntax variants:

```java
QEmployee employee = QEmployee.employee;
queryFactory.with(employee, SQLExpressions.select(employee.all)
                                          .from(employee)
                                          .where(employee.name.startsWith("A")))
            .from(...)
```

Using a column listing:

```java
QEmployee employee = QEmployee.employee;
queryFactory.with(employee, employee.id, employee.name)
            .as(SQLExpressions.select(employee.id, employee.name)
                              .from(employee)
                              .where(employee.name.startsWith("A")))
            .from(...)
```

If the columns of the common table expression are a subset of an existing table
or view, use a generated path type for it (e.g. `QEmployee`). Otherwise, use
`PathBuilder`:

```java
QEmployee employee = QEmployee.employee;
QDepartment department = QDepartment.department;
PathBuilder<Tuple> emp = new PathBuilder<Tuple>(Tuple.class, "emp");
queryFactory.with(emp, SQLExpressions.select(employee.id, employee.name, employee.departmentId,
                                          department.name.as("departmentName"))
                                      .from(employee)
                                      .innerJoin(department).on(employee.departmentId.eq(department.id)))
            .from(...)
```

## Data Manipulation Commands

### Insert

With columns:

```java
QSurvey survey = QSurvey.survey;

queryFactory.insert(survey)
    .columns(survey.id, survey.name)
    .values(3, "Hello").execute();
```

Without columns:

```java
queryFactory.insert(survey)
    .values(4, "Hello").execute();
```

With subquery:

```java
queryFactory.insert(survey)
    .columns(survey.id, survey.name)
    .select(SQLExpressions.select(survey2.id.add(1), survey2.name).from(survey2))
    .execute();
```

Using the `set` method:

```java
QSurvey survey = QSurvey.survey;

queryFactory.insert(survey)
    .set(survey.id, 3)
    .set(survey.name, "Hello").execute();
```

The `set` method always expands internally to columns and values.

To get the created keys instead of the modified rows count, use
`executeWithKey` / `executeWithKeys`.

To populate a clause based on the contents of a bean:

```java
queryFactory.insert(survey)
    .populate(surveyBean).execute();
```

This excludes null bindings. To include null bindings:

```java
queryFactory.insert(survey)
    .populate(surveyBean, DefaultMapper.WITH_NULL_BINDINGS).execute();
```

### Update

With where:

```java
QSurvey survey = QSurvey.survey;

queryFactory.update(survey)
    .where(survey.name.eq("XXX"))
    .set(survey.name, "S")
    .execute();
```

Without where:

```java
queryFactory.update(survey)
    .set(survey.name, "S")
    .execute();
```

Using bean population:

```java
queryFactory.update(survey)
    .populate(surveyBean)
    .execute();
```

### Delete

With where:

```java
QSurvey survey = QSurvey.survey;

queryFactory.delete(survey)
    .where(survey.name.eq("XXX"))
    .execute();
```

Without where:

```java
queryFactory.delete(survey)
    .execute();
```

## Batch Support in DML Clauses

Querydsl SQL supports JDBC batch updates through the DML APIs. Bundle
consecutive DML calls with a similar structure via `addBatch()`:

Update:

```java
QSurvey survey = QSurvey.survey;

queryFactory.insert(survey).values(2, "A").execute();
queryFactory.insert(survey).values(3, "B").execute();

SQLUpdateClause update = queryFactory.update(survey);
update.set(survey.name, "AA").where(survey.name.eq("A")).addBatch();
update.set(survey.name, "BB").where(survey.name.eq("B")).addBatch();
```

Delete:

```java
SQLDeleteClause delete = queryFactory.delete(survey);
delete.where(survey.name.eq("A")).addBatch();
delete.where(survey.name.eq("B")).addBatch();
delete.execute();
```

Insert:

```java
SQLInsertClause insert = queryFactory.insert(survey);
insert.set(survey.id, 5).set(survey.name, "5").addBatch();
insert.set(survey.id, 6).set(survey.name, "6").addBatch();
insert.execute();
```

## Bean Class Generation

To create JavaBean DTO types for the tables of your schema:

```java
java.sql.Connection conn = ...;
MetaDataExporter exporter = new MetaDataExporter();
exporter.setPackageName("com.myproject.mydomain");
exporter.setTargetFolder(new File("src/main/java"));
exporter.setBeanSerializer(new BeanSerializer());
exporter.export(conn.getMetaData());
```

Now you can use the bean types as arguments to the `populate` method in DML
clauses and you can project directly to bean types in queries:

```java
QEmployee e = new QEmployee("e");

// Insert
Employee employee = new Employee();
employee.setFirstname("John");
Integer id = queryFactory.insert(e).populate(employee).executeWithKey(e.id);
employee.setId(id);

// Update
employee.setLastname("Smith");
queryFactory.update(e).populate(employee).where(e.id.eq(employee.getId())).execute();

// Query
Employee smith = queryFactory.selectFrom(e).where(e.lastname.eq("Smith")).fetchOne();

// Delete
queryFactory.delete(e).where(e.id.eq(employee.getId())).execute();
```

## Extracting the SQL Query and Bindings

The SQL query and bindings can be extracted via `getSQL`:

```java
SQLBindings bindings = query.getSQL();
System.out.println(bindings.getSQL());
```

To include all literals in the SQL string, enable literal serialization on the
query or configuration level via `setUseLiterals(true)`.

## Custom Types

Querydsl SQL provides the possibility to declare custom type mappings for
`ResultSet`/`Statement` interaction. Custom type mappings can be declared in
`Configuration` instances:

```java
Configuration configuration = new Configuration(new H2Templates());
// overrides the mapping for Types.DATE
configuration.register(new UtilDateType());
```

For a table column:

```java
Configuration configuration = new Configuration(new H2Templates());
// declares a mapping for the gender column in the person table
configuration.register("person", "gender", new EnumByNameType<Gender>(Gender.class));
```

To customize a numeric mapping:

```java
configuration.registerNumeric(5, 2, Float.class);
```

This maps the `Float` type to the `NUMERIC(5,2)` type.

## Listening to Queries and Clauses

`SQLListener` is a listener interface that can be used to listen to queries and
DML clauses. `SQLListener` instances can be registered on the configuration or
on individual query/clause instances via the `addListener` method.

Use cases for listeners include data synchronization, logging, caching, and
validation.

## Spring Integration

Querydsl SQL integrates with Spring through the `querydsl-sql-spring` module:

```xml
<dependency>
  <groupId>{{ site.group_id }}</groupId>
  <artifactId>querydsl-sql-spring</artifactId>
  <version>{{ site.querydsl_version }}</version>
</dependency>
```

It provides Spring exception translation and a Spring connection provider for
usage of Querydsl SQL with Spring transaction managers:

```java
@Configuration
public class JdbcConfiguration {

    @Bean
    public DataSource dataSource() {
        // implementation omitted
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public com.querydsl.sql.Configuration querydslConfiguration() {
        SQLTemplates templates = H2Templates.builder().build();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

    @Bean
    public SQLQueryFactory queryFactory() {
        SpringConnectionProvider provider = new SpringConnectionProvider(dataSource());
        return new SQLQueryFactory(querydslConfiguration(), provider);
    }
}
```
