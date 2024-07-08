package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TypeAdapterTest {

  @Test
  public void Delegation() {
    Type inner = Types.OBJECT;
    Type type = new TypeAdapter(inner);
    assertThat(type.getCategory()).isEqualTo(inner.getCategory());
    assertThat(type.getComponentType()).isEqualTo(inner.getComponentType());
    assertThat(type.getFullName()).isEqualTo(inner.getFullName());
    assertThat(type.getGenericName(true)).isEqualTo(inner.getGenericName(true));
    assertThat(type.getPackageName()).isEqualTo(inner.getPackageName());
    assertThat(type.getParameters()).isEqualTo(inner.getParameters());
    //        assertEquals(inner.getPrimitiveName(), type.getPrimitiveName());
    assertThat(type.getSimpleName()).isEqualTo(inner.getSimpleName());
    assertThat(type.isFinal()).isFalse();
    assertThat(type.isPrimitive()).isFalse();
  }
}
