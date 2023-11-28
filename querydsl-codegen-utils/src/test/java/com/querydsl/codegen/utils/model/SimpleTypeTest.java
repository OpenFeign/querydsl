package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.Test;

public class SimpleTypeTest {

  public static class Inner {
    public class Inner2 {
      public class Inner3 {}
    }
  }

  @Test
  public void PrimitiveArray() {
    Type byteArray = new ClassType(byte[].class);
    Type byteArray2 = new SimpleType(byteArray, byteArray.getParameters());
    assertThat(
            byteArray.getRawName(
                Collections.singleton("java.lang"), Collections.<String>emptySet()))
        .isEqualTo("byte[]");
    assertThat(
            byteArray2.getRawName(
                Collections.singleton("java.lang"), Collections.<String>emptySet()))
        .isEqualTo("byte[]");
  }

  @Test
  public void Array_FullName() {
    Type type = new SimpleType(new ClassType(String[].class));
    assertThat(type.getFullName()).isEqualTo("java.lang.String[]");
  }

  @Test
  public void GetComponentType() {
    Type type = new SimpleType(new ClassType(String[].class));
    assertThat(type.getComponentType().getFullName()).isEqualTo("java.lang.String");
  }

  @Test
  public void GetRawName() {
    assertThat(
            new SimpleType(Types.STRING)
                .getRawName(
                    Collections.<String>emptySet(),
                    Collections.singleton(Types.STRING.getFullName())))
        .isEqualTo("String");
  }

  @Test
  public void GetJavaClass_For_Array() {
    System.out.println(Inner.class.getName());
    assertThat(new ClassType(byte[].class).getJavaClass()).isEqualTo(byte[].class);
    assertThat(new SimpleType(new ClassType(byte[].class)).getJavaClass()).isEqualTo(byte[].class);
  }

  @Test
  public void GetJavaClass_For_InnerClass() {
    assertThat(new ClassType(Inner.class).getJavaClass()).isEqualTo(Inner.class);
    assertThat(new SimpleType(new ClassType(Inner.class)).getJavaClass()).isEqualTo(Inner.class);
  }

  //    @Test
  //    public void GetPrimitiveName() {
  //        assertEquals("int", Types.INT.getPrimitiveName());
  //        assertEquals("int", new SimpleType(Types.INT).getPrimitiveName());
  //
  //        assertEquals("int", Types.INTEGER.getPrimitiveName());
  //        assertEquals("int", new SimpleType(Types.INTEGER).getPrimitiveName());
  //    }

  @Test
  public void GetEnclosingType() {
    Type outer = new SimpleType(new ClassType(SimpleTypeTest.class));
    Type inner = new SimpleType(new ClassType(SimpleTypeTest.Inner.class));
    Type inner2 = new SimpleType(new ClassType(SimpleTypeTest.Inner.Inner2.class));
    Type inner3 = new SimpleType(new ClassType(SimpleTypeTest.Inner.Inner2.Inner3.class));

    assertThat(inner3.getEnclosingType()).isEqualTo(inner2);
    assertThat(inner2.getEnclosingType()).isEqualTo(inner);
    assertThat(inner.getEnclosingType()).isEqualTo(outer);
    assertThat(outer.getEnclosingType()).isNull();

    assertThat(
            inner3.getRawName(
                Collections.singleton(outer.getPackageName()), Collections.emptySet()))
        .isEqualTo("SimpleTypeTest.Inner.Inner2.Inner3");
    assertThat(
            inner3.getRawName(Collections.emptySet(), Collections.singleton(inner2.getFullName())))
        .isEqualTo("Inner2.Inner3");
    assertThat(
            inner3.getRawName(Collections.emptySet(), Collections.singleton(inner3.getFullName())))
        .isEqualTo("Inner3");
  }

  @Test
  public void IsMember() {
    assertThat(new SimpleType(new ClassType(SimpleTypeTest.Inner.class)).isMember()).isTrue();
    assertThat(new SimpleType(new ClassType(SimpleType.class)).isMember()).isFalse();
  }
}
