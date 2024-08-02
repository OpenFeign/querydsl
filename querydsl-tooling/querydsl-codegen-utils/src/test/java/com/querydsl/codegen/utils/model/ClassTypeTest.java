/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import org.junit.Test;

public class ClassTypeTest {

  public class Inner {
    public class Inner2 {
      public class Inner3 {}
    }
  }

  private final ClassType stringType = new ClassType(TypeCategory.STRING, String.class);

  // @Test
  // public void asArrayType(){
  // assertEquals(stringType, stringType.asArrayType().getParameter(0));
  // }

  @Test
  public void InnerClass_Name() {
    assertThat(new ClassType(Inner.class).getFullName())
        .isEqualTo("com.querydsl.codegen.utils.model.ClassTypeTest.Inner");
    assertThat(new ClassType(Inner.class).asArrayType().getFullName())
        .isEqualTo("com.querydsl.codegen.utils.model.ClassTypeTest.Inner[]");
  }

  @Test
  public void ArrayType() {
    Type type = new ClassType(TypeCategory.ARRAY, String[].class);
    assertThat(type.getPackageName()).isEqualTo("java.lang");
  }

  @Test
  public void ArrayType_Equals_SimpleType() {
    Type type = new ClassType(TypeCategory.ARRAY, String[].class);
    Type type2 = new SimpleType("java.lang.String[]", "java.lang", "String[]");
    assertThat(type2).isEqualTo(type);
  }

  @Test
  public void As() {
    assertThat(stringType.as(TypeCategory.COMPARABLE).getCategory())
        .isEqualTo(TypeCategory.COMPARABLE);
  }

  @Test
  public void GetParameters() {
    var mapType = new ClassType(TypeCategory.MAP, Map.class, stringType, stringType);
    assertThat(mapType.getParameters()).hasSize(2);
    assertThat(mapType.getParameters().getFirst()).isEqualTo(stringType);
    assertThat(mapType.getParameters().get(1)).isEqualTo(stringType);
    // assertEquals(stringType, mapType.getSelfOrValueType());
    assertThat(mapType.isPrimitive()).isFalse();
  }

  @Test
  public void GetComponentType() {
    assertThat(new ClassType(String[].class).getComponentType().getFullName())
        .isEqualTo("java.lang.String");
  }

  @Test
  public void Primitive_Arrays() {
    var byteArray = new ClassType(byte[].class);
    assertThat(
            byteArray.getRawName(
                Collections.singleton("java.lang"), Collections.<String>emptySet()))
        .isEqualTo("byte[]");
    assertThat(byteArray.getSimpleName()).isEqualTo("byte[]");
    assertThat(byteArray.getFullName()).isEqualTo("byte[]");
  }

  @Test
  public void Array() {
    var byteArray = new ClassType(Byte[].class);
    assertThat(
            byteArray.getRawName(
                Collections.singleton("java.lang"), Collections.<String>emptySet()))
        .isEqualTo("Byte[]");
    assertThat(byteArray.getSimpleName()).isEqualTo("Byte[]");
    assertThat(byteArray.getFullName()).isEqualTo("java.lang.Byte[]");
  }

  @Test
  public void IsPrimitive() {
    assertThat(Types.CHAR.isPrimitive()).isTrue();
    assertThat(Types.DOUBLE_P.isPrimitive()).isTrue();
    assertThat(Types.FLOAT_P.isPrimitive()).isTrue();
    assertThat(Types.INT.isPrimitive()).isTrue();
    assertThat(Types.LONG_P.isPrimitive()).isTrue();
    assertThat(Types.SHORT_P.isPrimitive()).isTrue();
  }

  //    @Test
  //    public void GetPrimitiveName() {
  //        assertEquals("char", Types.CHARACTER.getPrimitiveName());
  //        assertEquals("double", Types.DOUBLE.getPrimitiveName());
  //        assertEquals("float", Types.FLOAT.getPrimitiveName());
  //        assertEquals("int", Types.INTEGER.getPrimitiveName());
  //        assertEquals("long", Types.LONG.getPrimitiveName());
  //        assertEquals("short", Types.SHORT.getPrimitiveName());
  //    }

  @Test
  public void GetEnclosingType() {
    Type outer = new ClassType(ClassTypeTest.class);
    Type inner = new ClassType(ClassTypeTest.Inner.class);
    Type inner2 = new ClassType(ClassTypeTest.Inner.Inner2.class);
    Type inner3 = new ClassType(ClassTypeTest.Inner.Inner2.Inner3.class);

    assertThat(inner3.getEnclosingType()).isEqualTo(inner2);
    assertThat(inner2.getEnclosingType()).isEqualTo(inner);
    assertThat(inner.getEnclosingType()).isEqualTo(outer);
    assertThat(outer.getEnclosingType()).isNull();

    assertThat(
            inner3.getRawName(
                Collections.singleton(outer.getPackageName()), Collections.emptySet()))
        .isEqualTo("ClassTypeTest.Inner.Inner2.Inner3");
    assertThat(
            inner3.getRawName(Collections.emptySet(), Collections.singleton(inner2.getFullName())))
        .isEqualTo("Inner2.Inner3");
    assertThat(
            inner3.getRawName(Collections.emptySet(), Collections.singleton(inner3.getFullName())))
        .isEqualTo("Inner3");
  }
}
