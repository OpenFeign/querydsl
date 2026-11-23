## FluentQ Collections

The Collections module provides integration with Java Collections and Beans.

**Maven integration**

 Add the following dependencies to your Maven project :

```XML
<dependency>
  <groupId>fluentq</groupId>
  <artifactId>fluentq-apt</artifactId>
  <version>${fluentq.version}</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>fluentq</groupId>
  <artifactId>fluentq-collections</artifactId>
  <version>${fluentq.version}</version>
</dependency>
```

If you are not using JPA or JDO you can generate FluentQ query types for your domain types by annotating them with the fluentq.core.annotations.QueryEntity annotation and adding the following plugin configuration into your Maven configuration (pom.xml) :

```XML
<project>
  <build>
    <plugins>
      ...
      <plugin>
        <groupId>com.mysema.maven</groupId>
        <artifactId>apt-maven-plugin</artifactId>
        <version>1.1.3</version>
        <executions>
          <execution>
            <goals>
              <goal>process</goal>
            </goals>
            <configuration>
              <outputDirectory>target/generated-sources/java</outputDirectory>
              <processor>fluentq.apt.FluentQAnnotationProcessor</processor>
            </configuration>
          </execution>
        </executions>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```

**Querying**

Querying with FluentQ Collections is as simple as this :

```JAVA
import static fluentq.collections.CollQueryFactory.*;

QCat cat = new QCat("cat");
for (String name : from(cat,cats)
  .select(cat.name)
  .where(cat.kittens.size().gt(0))
  .fetch()){
    System.out.println(name);
}
```

For more information on the FluentQ Collections module visit the reference documentation http://www.fluentq.com/static/fluentq/latest/reference/html/ch02s08.html
