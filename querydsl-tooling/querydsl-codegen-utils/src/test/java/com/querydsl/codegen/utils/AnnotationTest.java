/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import org.junit.Test;

@Annotation(prop2 = false, clazz = AnnotationTest.class)
@Annotation2("Hello")
@Annotation3(type = ElementType.ANNOTATION_TYPE)
public class AnnotationTest {

  private StringWriter w = new StringWriter();
  private CodeWriter writer = new JavaWriter(w);

  @Test
  public void ClassAnnotation() throws IOException {
    writer.annotation(getClass().getAnnotation(Annotation.class));
    String option1 =
        "@com.querydsl.codegen.utils.Annotation(clazz=com.querydsl.codegen.utils.AnnotationTest.class,"
            + " prop2=false)";
    String option2 =
        "@com.querydsl.codegen.utils.Annotation(prop2=false,"
            + " clazz=com.querydsl.codegen.utils.AnnotationTest.class)";
    String serialized = w.toString().trim();
    assertThat(serialized.equals(option1) || serialized.equals(option2)).isTrue();
  }

  @Test
  public void ClassAnnotation2() throws IOException {
    writer.annotation(getClass().getAnnotation(Annotation2.class));
    assertThat(w.toString().trim()).isEqualTo("@com.querydsl.codegen.utils.Annotation2(\"Hello\")");
  }

  @Test
  public void ClassAnnotation3() throws IOException {
    writer.annotation(getClass().getAnnotation(Annotation3.class));
    assertThat(w.toString().trim())
        .isEqualTo(
            "@com.querydsl.codegen.utils.Annotation3(type=java.lang.annotation.ElementType.ANNOTATION_TYPE)");
  }

  @Test
  public void MethodAnnotation() throws IOException, SecurityException, NoSuchMethodException {
    writer.annotation(getClass().getMethod("MethodAnnotation").getAnnotation(Test.class));
    assertThat(w.toString().trim()).isEqualTo("@org.junit.Test");
  }

  @Test
  public void Min() throws IOException {
    writer.annotation(new MinImpl(10));
    assertThat(w.toString().trim())
        .isEqualTo(
            "@jakarta.validation.constraints.Min(value=10, message=\"{javax.validation.constraints.Min.message}\")");
  }

  @Test
  public void Max() throws IOException {
    writer.annotation(new MaxImpl(10));
    assertThat(w.toString().trim())
        .isEqualTo(
            "@jakarta.validation.constraints.Max(value=10, message=\"{javax.validation.constraints.Max.message}\")");
  }

  @Test
  public void NotNull() throws IOException {
    writer.annotation(new NotNullImpl());
    assertThat(w.toString().trim())
        .isEqualTo(
            "@jakarta.validation.constraints.NotNull(message=\"{javax.validation.constraints.NotNull.message}\")");
  }

  @Test
  public void Uri_Value() throws IOException {
    writer.annotation(new Annotation2Impl("http://www.example.com#"));
    assertThat(w.toString().trim())
        .isEqualTo("@com.querydsl.codegen.utils.Annotation2(\"http://www.example.com#\")");
  }
}
