package com.querydsl.apt.domain;

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QuerySupertype;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class Generic8Test {

  @QuerySupertype
  public static class Superclass<T> {

    Long id;

    List<T> values;

    List<? extends T> values2;
  }

  @QueryEntity
  public static class IntermediateEntity<E> extends Superclass<E> {}

  @QueryEntity
  public static class Entity extends Superclass<String> {}

  @QueryEntity
  public static class Entity2 extends Superclass<Integer> {}

  @QueryEntity
  public static class Entity3 extends IntermediateEntity<String> {}

  @Test
  public void test() {
    Assertions.assertEquals(String.class, QGeneric8Test_Entity.entity.values.getElementType());
    Assertions.assertEquals(Integer.class, QGeneric8Test_Entity2.entity2.values.getElementType());
    Assertions.assertEquals(String.class, QGeneric8Test_Entity3.entity3.values.getElementType());
  }
}
