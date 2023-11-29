package com.querydsl.codegen;

import static org.junit.Assert.assertEquals;

import com.querydsl.codegen.utils.model.Type;
import com.querydsl.core.annotations.QueryEntity;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class Generic2Test {

  @QueryEntity
  public static class AbstractCollectionAttribute<T extends Collection<?>> {

    T value;
  }

  @QueryEntity
  public static class ListAttribute<T> extends AbstractCollectionAttribute<List<T>> {

    String name;
  }

  @QueryEntity
  public static class Product {

    ListAttribute<Integer> integerAttributes;
    ListAttribute<String> stringAttributes;
  }

  @Test
  public void resolve() {
    TypeFactory factory = new TypeFactory(Collections.<Class<? extends Annotation>>emptyList());
    Type type = factory.get(AbstractCollectionAttribute.class);
    assertEquals(
        "com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute",
        type.getGenericName(false));
    assertEquals(
        "com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute", type.getGenericName(true));
  }

  @Test
  public void resolve2() {
    TypeFactory factory = new TypeFactory(Collections.<Class<? extends Annotation>>emptyList());
    Type type = factory.getEntityType(AbstractCollectionAttribute.class);
    assertEquals(
        "com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute<? extends java.util.Collection<?>>",
        type.getGenericName(false));
    assertEquals(
        "com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute<? extends java.util.Collection<?>>",
        type.getGenericName(true));
  }

  @Test
  public void export() {
    GenericExporter exporter = new GenericExporter();
    exporter.setTargetFolder(new File("target/Generic2Test"));
    exporter.export(Generic2Test.class.getClasses());
  }
}
