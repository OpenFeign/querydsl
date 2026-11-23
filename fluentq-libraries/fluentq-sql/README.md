## FluentQ SQL

The SQL module provides integration with the JDBC API.

**Maven integration**

 Add the following dependencies to your Maven project :

```XML
<dependency>
  <groupId>fluentq</groupId>
  <artifactId>fluentq-sql</artifactId>
  <version>${fluentq.version}</version>
</dependency>
```

**Code generation via Maven**

This functionality is also available as a Maven plugin. The presented example can be declared like this in the POM :

```XML
<project>
  <build>
    <plugins>
      ...
      <plugin>
        <groupId>fluentq</groupId>
        <artifactId>fluentq-maven-plugin</artifactId>
        <version>${fluentq.version}</version>
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
      ...
    </plugins>
  </build>
</project>
```

Use the goal test-export to add the targetFolder as a test compile source root instead of a compile source root.

**Querying**

Querying with FluentQ SQL is as simple as this :

```JAVA 
QCustomer customer = new QCustomer("c");

SQLTemplates dialect = new HSQLDBTemplates(); // SQL-dialect
SQLQuery<?> query = new SQLQuery<Void>(connection, dialect);
List<String> lastNames = query.select(customer.lastName)
    .from(customer)
    .where(customer.firstName.eq("Bob"))
    .fetch();
```
For more information on the FluentQ SQL module visit the reference documentation http://www.fluentq.com/static/fluentq/latest/reference/html/ch02s03.html
