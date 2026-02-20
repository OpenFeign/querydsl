package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import org.junit.Test;

public class QueryInit5Test {

  @QueryEntity
  public static class OtherClass {
    OtherClass entity;
  }

  @QueryEntity
  public static class OtherClassTwo {
    OtherClassTwo entity;
  }

  @QueryEntity
  public static class Parent {
    int x;

    @QueryInit("*")
    OtherClass z;
  }

  @QueryEntity
  public static class Child extends Parent {
    @QueryInit("*")
    OtherClassTwo y;
  }

  @Test
  public void test() {
    // QChild c = QParent.parent.as(QChild.class)
    assertThat(QQueryInit5Test_Parent.parent.z.entity).isNotNull();

    var child = QQueryInit5Test_Parent.parent.as(QQueryInit5Test_Child.class);
    assertThat(child.z.entity).isNotNull();
    assertThat(child.y.entity).isNotNull();
  }
}
