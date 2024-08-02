package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;

public class InnerClassesTest {

  public static class Entity {}

  @Test
  public void DirectParameter() throws IOException {
    Type entityType = new ClassType(Entity.class);
    Type type =
        new SimpleType(
            "com.querydsl.codegen.utils.gen.QEntity",
            "com.querydsl.codegen.utils.gen",
            "QEntity",
            entityType);

    var str = new StringWriter();
    var writer = new JavaWriter(str);
    writer.beginClass(type);
    writer.end();

    System.err.println(str.toString());
  }

  @Test
  public void Java() {
    var str = new StringWriter();
    var writer = new JavaWriter(str);

    assertThat(writer.getRawName(new ClassType(Entity.class)))
        .isEqualTo("com.querydsl.codegen.utils.InnerClassesTest.Entity");
  }

  @Test
  public void Scala() {
    var str = new StringWriter();
    var writer = new ScalaWriter(str);

    assertThat(writer.getRawName(new ClassType(Entity.class)))
        .isEqualTo("com.querydsl.codegen.utils.InnerClassesTest$Entity");
  }
}
