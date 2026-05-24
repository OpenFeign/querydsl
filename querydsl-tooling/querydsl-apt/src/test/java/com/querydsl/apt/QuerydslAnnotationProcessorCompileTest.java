package com.querydsl.apt;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class QuerydslAnnotationProcessorCompileTest {

  @Test
  void queryEntity_generatesQClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.MyEntity",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class MyEntity {
              public String name;
              public int count;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new QuerydslAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QMyEntity")).isPresent();
  }

  @Test
  void querySupertype_generatesQClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.MySupertype",
            """
            package test;

            import com.querydsl.core.annotations.QuerySupertype;

            @QuerySupertype
            public class MySupertype {
              public String id;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new QuerydslAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QMySupertype")).isPresent();
  }

  @Test
  void queryEmbeddable_generatesQClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.MyEmbeddable",
            """
            package test;

            import com.querydsl.core.annotations.QueryEmbeddable;

            @QueryEmbeddable
            public class MyEmbeddable {
              public String street;
              public String city;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new QuerydslAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QMyEmbeddable")).isPresent();
  }

  @Test
  void generatedQClass_containsStringField() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.Person",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class Person {
              public String name;
              public int age;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new QuerydslAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    var generated = compilation.generatedSourceFile("test.QPerson").orElseThrow();
    var content = generated.getCharContent(false).toString();
    assertThat(content).contains("StringPath name");
    assertThat(content).contains("NumberPath");
  }

  @Test
  void unannotatedClass_noQClassGenerated() {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.PlainClass",
            """
            package test;

            public class PlainClass {
              public String field;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new QuerydslAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QPlainClass")).isEmpty();
  }

  @Test
  void circularQClassReference_producesWarning() {
    JavaFileObject orderSource =
        JavaFileObjects.forSourceString(
            "test.Order",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class Order {
              public Customer customer;
            }
            """);

    JavaFileObject customerSource =
        JavaFileObjects.forSourceString(
            "test.Customer",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class Customer {
              public Order lastOrder;
            }
            """);

    Compilation compilation =
        javac()
            .withProcessors(new QuerydslAnnotationProcessor())
            .compile(orderSource, customerSource);

    CompilationSubject.assertThat(compilation).succeeded();
    CompilationSubject.assertThat(compilation)
        .hadWarningContaining("Circular Q-class references detected");
  }

  @Test
  void unidirectionalReference_noWarning() {
    JavaFileObject orderSource =
        JavaFileObjects.forSourceString(
            "test.Order",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class Order {
              public Customer customer;
            }
            """);

    JavaFileObject customerSource =
        JavaFileObjects.forSourceString(
            "test.Customer",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class Customer {
              public String name;
            }
            """);

    Compilation compilation =
        javac()
            .withProcessors(new QuerydslAnnotationProcessor())
            .compile(orderSource, customerSource);

    CompilationSubject.assertThat(compilation).succeeded();
    CompilationSubject.assertThat(compilation).hadWarningCount(0);
  }

  @Test
  void collectionReference_noWarning() {
    JavaFileObject orderSource =
        JavaFileObjects.forSourceString(
            "test.Order",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;
            import java.util.List;

            @QueryEntity
            public class Order {
              public List<OrderItem> items;
            }
            """);

    JavaFileObject orderItemSource =
        JavaFileObjects.forSourceString(
            "test.OrderItem",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class OrderItem {
              public Order order;
            }
            """);

    Compilation compilation =
        javac()
            .withProcessors(new QuerydslAnnotationProcessor())
            .compile(orderSource, orderItemSource);

    CompilationSubject.assertThat(compilation).succeeded();
    CompilationSubject.assertThat(compilation).hadWarningCount(0);
  }

  @Test
  void indirectCircularReference_producesWarning() {
    JavaFileObject aSource =
        JavaFileObjects.forSourceString(
            "test.EntityA",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class EntityA {
              public EntityB b;
            }
            """);

    JavaFileObject bSource =
        JavaFileObjects.forSourceString(
            "test.EntityB",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class EntityB {
              public EntityC c;
            }
            """);

    JavaFileObject cSource =
        JavaFileObjects.forSourceString(
            "test.EntityC",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class EntityC {
              public EntityA a;
            }
            """);

    Compilation compilation =
        javac()
            .withProcessors(new QuerydslAnnotationProcessor())
            .compile(aSource, bSource, cSource);

    CompilationSubject.assertThat(compilation).succeeded();
    CompilationSubject.assertThat(compilation)
        .hadWarningContaining("Circular Q-class references detected");
  }

  @Test
  void entityWithInheritance_generatesQClasses() {
    JavaFileObject superSource =
        JavaFileObjects.forSourceString(
            "test.BaseEntity",
            """
            package test;

            import com.querydsl.core.annotations.QuerySupertype;

            @QuerySupertype
            public class BaseEntity {
              public Long id;
            }
            """);

    JavaFileObject subSource =
        JavaFileObjects.forSourceString(
            "test.ChildEntity",
            """
            package test;

            import com.querydsl.core.annotations.QueryEntity;

            @QueryEntity
            public class ChildEntity extends BaseEntity {
              public String childField;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new QuerydslAnnotationProcessor()).compile(superSource, subSource);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QBaseEntity")).isPresent();
    assertThat(compilation.generatedSourceFile("test.QChildEntity")).isPresent();
  }
}
