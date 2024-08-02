package com.querydsl.codegen;

import static org.assertj.core.api.Assertions.assertThat;

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
    var factory = new TypeFactory(Collections.<Class<? extends Annotation>>emptyList());
    var type = factory.get(AbstractCollectionAttribute.class);
    assertThat(type.getGenericName(false))
        .isEqualTo("com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute");
    assertThat(type.getGenericName(true))
        .isEqualTo("com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute");
  }

  @Test
  public void resolve2() {
    var factory = new TypeFactory(Collections.<Class<? extends Annotation>>emptyList());
    Type type = factory.getEntityType(AbstractCollectionAttribute.class);
    assertThat(type.getGenericName(false))
        .isEqualTo(
            """
            com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute<? extends\
             java.util.Collection<?>>\
            """);
    assertThat(type.getGenericName(true))
        .isEqualTo(
            """
            com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute<? extends\
             java.util.Collection<?>>\
            """);
  }

  @Test
  public void export() {
    var exporter = new GenericExporter();
    exporter.setTargetFolder(new File("target/Generic2Test"));
    exporter.export(Generic2Test.class.getClasses());
  }
}
