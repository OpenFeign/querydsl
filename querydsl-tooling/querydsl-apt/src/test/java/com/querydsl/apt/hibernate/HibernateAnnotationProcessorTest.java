package com.querydsl.apt.hibernate;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class HibernateAnnotationProcessorTest {

  @Test
  void jpaEntity_generatesQClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceLines(
            "test.Product",
            "package test;",
            "",
            "import jakarta.persistence.Entity;",
            "import jakarta.persistence.Id;",
            "",
            "@Entity",
            "public class Product {",
            "  @Id public Long id;",
            "  public String name;",
            "  public double price;",
            "}");

    Compilation compilation =
        javac().withProcessors(new HibernateAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QProduct")).isPresent();
  }

  @Test
  void hibernateFormula_handledCorrectly() {
    JavaFileObject source =
        JavaFileObjects.forSourceLines(
            "test.Account",
            "package test;",
            "",
            "import jakarta.persistence.Entity;",
            "import jakarta.persistence.Id;",
            "import org.hibernate.annotations.Formula;",
            "",
            "@Entity",
            "public class Account {",
            "  @Id public Long id;",
            "  public double balance;",
            "  @Formula(\"balance * 1.1\")",
            "  public double projectedBalance;",
            "}");

    Compilation compilation =
        javac().withProcessors(new HibernateAnnotationProcessor()).compile(source);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QAccount")).isPresent();
  }

  @Test
  void entityWithMappedSuperclass_generatesQClasses() {
    JavaFileObject superSource =
        JavaFileObjects.forSourceLines(
            "test.AbstractEntity",
            "package test;",
            "",
            "import jakarta.persistence.Id;",
            "import jakarta.persistence.MappedSuperclass;",
            "",
            "@MappedSuperclass",
            "public abstract class AbstractEntity {",
            "  @Id public Long id;",
            "}");

    JavaFileObject subSource =
        JavaFileObjects.forSourceLines(
            "test.Item",
            "package test;",
            "",
            "import jakarta.persistence.Entity;",
            "",
            "@Entity",
            "public class Item extends AbstractEntity {",
            "  public String title;",
            "}");

    Compilation compilation =
        javac().withProcessors(new HibernateAnnotationProcessor()).compile(superSource, subSource);

    CompilationSubject.assertThat(compilation).succeeded();
    assertThat(compilation.generatedSourceFile("test.QAbstractEntity")).isPresent();
    assertThat(compilation.generatedSourceFile("test.QItem")).isPresent();
  }
}
