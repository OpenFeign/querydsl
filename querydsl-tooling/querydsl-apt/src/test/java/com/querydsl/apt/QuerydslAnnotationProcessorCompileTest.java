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
