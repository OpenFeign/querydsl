package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TypeSuperTest {

  @Test
  public void GetVarName() {
    assertThat(new TypeSuper("var", Types.STRING).getVarName()).isEqualTo("var");
  }

  @Test
  public void GetGenericName() {
    assertThat(new TypeSuper(Types.STRING).getGenericName(false))
        .isEqualTo("? super java.lang.String");
  }

  @Test
  public void GetGenericName_As_ArgType() {
    assertThat(new TypeSuper(Types.STRING).getGenericName(true)).isEqualTo("java.lang.Object");
  }

  @Test
  public void Comparable() {
    // T extends Comparable<? super T>
    Type comparable = new ClassType(Comparable.class);
    Type type =
        new TypeExtends(
            "T", new SimpleType(comparable, new TypeSuper(new TypeExtends("T", comparable))));
    assertThat(type.getGenericName(false)).isEqualTo("? extends java.lang.Comparable<?>");
  }
}
