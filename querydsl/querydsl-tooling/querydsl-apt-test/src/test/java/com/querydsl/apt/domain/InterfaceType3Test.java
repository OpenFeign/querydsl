package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import org.junit.Test;

public class InterfaceType3Test {

  @QueryEntity
  public interface A {
    String getA();
  }

  @QueryEntity
  public interface B {
    String getB();
  }

  @QueryEntity
  public interface C extends A, B {
    String getC();
  }

  @Test
  public void test() {
    assertThat(QInterfaceType3Test_C.c1.a).isNotNull();
    assertThat(QInterfaceType3Test_C.c1.b).isNotNull();
    assertThat(QInterfaceType3Test_C.c1.c).isNotNull();
  }
}
