package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TypeExtendsTest {

  @Test
  public void GetVarName() {
    assertThat(new TypeExtends("var", Types.COLLECTION).getVarName()).isEqualTo("var");
  }

  @Test
  public void GetGenericName() {
    assertThat(new TypeExtends(Types.COLLECTION).getGenericName(false))
        .isEqualTo("? extends java.util.Collection<java.lang.Object>");
  }

  @Test
  public void GetGenericName_As_ArgType() {
    assertThat(new TypeExtends(Types.COLLECTION).getGenericName(true))
        .isEqualTo("java.util.Collection<java.lang.Object>");
  }

  @Test
  public void GetGenericName_With_Object() {
    assertThat(new TypeExtends(Types.OBJECT).getGenericName(false)).isEqualTo("?");
  }
}
