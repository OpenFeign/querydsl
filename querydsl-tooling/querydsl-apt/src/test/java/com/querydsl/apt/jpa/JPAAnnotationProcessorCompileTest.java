package com.querydsl.apt.jpa;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class JPAAnnotationProcessorCompileTest {

  @Test
  void jpaEntity_generatesQClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.Customer",
            """
            package test;

            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;

            @Entity
            public class Customer {
              @Id public Long id;
              public String name;
            }
            """);

    Compilation compilation = javac().withProcessors(new JPAAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QCustomer")).isPresent();
  }

  @Test
  void mappedSuperclass_generatesQClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.BaseEntity",
            """
            package test;

            import jakarta.persistence.MappedSuperclass;
            import jakarta.persistence.Id;

            @MappedSuperclass
            public class BaseEntity {
              @Id public Long id;
            }
            """);

    Compilation compilation = javac().withProcessors(new JPAAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QBaseEntity")).isPresent();
  }

  @Test
  void embeddable_generatesQClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString(
            "test.Address",
            """
            package test;

            import jakarta.persistence.Embeddable;

            @Embeddable
            public class Address {
              public String street;
              public String city;
            }
            """);

    Compilation compilation = javac().withProcessors(new JPAAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QAddress")).isPresent();
  }

  @Test
  void entityWithEmbedded_generatesQClasses() {
    JavaFileObject embeddable =
        JavaFileObjects.forSourceString(
            "test.Address",
            """
            package test;

            import jakarta.persistence.Embeddable;

            @Embeddable
            public class Address {
              public String street;
            }
            """);

    JavaFileObject entity =
        JavaFileObjects.forSourceString(
            "test.Person",
            """
            package test;

            import jakarta.persistence.Embedded;
            import jakarta.persistence.Entity;
            import jakarta.persistence.Id;

            @Entity
            public class Person {
              @Id public Long id;
              @Embedded public Address address;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new JPAAnnotationProcessor()).compile(embeddable, entity);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QAddress")).isPresent();
    assertThat(compilation.generatedSourceFile("test.QPerson")).isPresent();
  }

  @Test
  void entityWithInheritance_generatesQClasses() {
    JavaFileObject superSource =
        JavaFileObjects.forSourceString(
            "test.BaseEntity",
            """
            package test;

            import jakarta.persistence.Id;
            import jakarta.persistence.MappedSuperclass;

            @MappedSuperclass
            public class BaseEntity {
              @Id public Long id;
            }
            """);

    JavaFileObject subSource =
        JavaFileObjects.forSourceString(
            "test.Order",
            """
            package test;

            import jakarta.persistence.Entity;

            @Entity
            public class Order extends BaseEntity {
              public String description;
            }
            """);

    Compilation compilation =
        javac().withProcessors(new JPAAnnotationProcessor()).compile(superSource, subSource);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QBaseEntity")).isPresent();
    assertThat(compilation.generatedSourceFile("test.QOrder")).isPresent();
  }
}
